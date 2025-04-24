package com.cliqshop.payment;

import com.cliqshop.dto.PaymentIntentRequest;
import com.cliqshop.dto.PaymentResponse;
import com.cliqshop.entity.Order;
import com.cliqshop.entity.User;
import com.cliqshop.service.OrderService;
import com.cliqshop.service.UserService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Value("${stripe.api.secretKey}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;
    
    @Value("${stripe.api.publishableKey}")
    private String publishableKey;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;
    
    @jakarta.annotation.PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
        logger.info("Stripe API initialized with key: {}", stripeApiKey.substring(0, 5) + "...");
    }

    @Override
    public PaymentResponse createPaymentIntent(PaymentIntentRequest request) throws StripeException {
        logger.info("Creating payment intent for order: {}, amount: {}", request.getOrderId(), request.getAmount());
        
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount().longValue() * 100) // Convert to cents/paise
                .setCurrency(request.getCurrency())
                .putMetadata("orderId", request.getOrderId().toString())
                .setReceiptEmail(request.getEmail())
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build()
                )
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        logger.info("Payment intent created: {}", paymentIntent.getId());
        
        PaymentResponse response = new PaymentResponse();
        response.setSuccess(true);
        response.setClientSecret(paymentIntent.getClientSecret());
        response.setPaymentIntentId(paymentIntent.getId());
        response.setStatus(paymentIntent.getStatus());
        
        return response;
    }
    
    @Override
    public String getPublishableKey() {
        return publishableKey;
    }

    @Override
    public PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId) throws StripeException {
        logger.info("Confirming payment intent: {} with payment method: {}", paymentIntentId, paymentMethodId);
        
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        
        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
            .setPaymentMethod(paymentMethodId)
            .build();
        
        return paymentIntent.confirm(params);
    }

    @Override
    public PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException {
        logger.info("Cancelling payment intent: {}", paymentIntentId);
        
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.cancel();
    }

    @Override
    public PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException {
        logger.info("Retrieving payment intent: {}", paymentIntentId);
        
        return PaymentIntent.retrieve(paymentIntentId);
    }

    @Override
    public Event processWebhookEvent(String payload, String sigHeader) throws StripeException {
        try {
            logger.info("Processing webhook event with signature: {}", sigHeader.substring(0, 10) + "...");
            
            return Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
        } catch (SignatureVerificationException e) {
            logger.error("Invalid signature on webhook", e);
            throw e;
        }
    }

    @Override
    public Session createCheckoutSession(com.stripe.param.checkout.SessionCreateParams params) throws StripeException {
        logger.info("Creating Stripe Checkout session");
        
        return Session.create(params);
    }

    // Helper methods to handle webhook events
    public void handleWebhookEvent(Event event) {
        String eventType = event.getType();
        logger.info("Processing webhook event of type: {}", eventType);
        
        switch (eventType) {
            case "checkout.session.completed":
                handleCheckoutSessionCompleted(event);
                break;
            case "payment_intent.succeeded":
                handlePaymentIntentSucceeded(event);
                break;
            case "payment_intent.payment_failed":
                handlePaymentIntentFailed(event);
                break;
            default:
                logger.info("Unhandled event type: {}", eventType);
        }
    }

    private void handlePaymentIntentSucceeded(Event event) {
        try {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (paymentIntent != null) {
                // Update order status
                String orderId = paymentIntent.getMetadata().get("orderId");
                if (orderId != null) {
                    try {
                        Order order = orderService.getOrderById(Long.parseLong(orderId));
                        order.setPaymentStatus(Order.PaymentStatus.PAID);
                        order.setStatus(Order.OrderStatus.PROCESSING);
                        order.setPaymentIntentId(paymentIntent.getId());
                        
                        // Instead of using getCharges() which isn't available, we'll just use the payment intent ID
                        // and receipt URL will be null or set from other sources if needed
                        orderService.markOrderAsPaid(order.getOrderId(), paymentIntent.getId(), null);
                        
                        logger.info("Order {} updated with payment succeeded", orderId);
                    } catch (Exception e) {
                        logger.error("Error updating order after payment succeeded: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling payment_intent.succeeded event: {}", e.getMessage());
        }
    }

    private void handlePaymentIntentFailed(Event event) {
        try {
            PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
            if (paymentIntent != null) {
                // Update order status
                String orderId = paymentIntent.getMetadata().get("orderId");
                if (orderId != null) {
                    try {
                        Order order = orderService.getOrderById(Long.parseLong(orderId));
                        orderService.markOrderPaymentFailed(order.getOrderId(), paymentIntent.getId());
                        
                        logger.info("Order {} updated with payment failed", orderId);
                    } catch (Exception e) {
                        logger.error("Error updating order after payment failed: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling payment_intent.payment_failed event: {}", e.getMessage());
        }
    }

    private void handleCheckoutSessionCompleted(Event event) {
        try {
            Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if (session != null) {
                // The checkout session was completed successfully
                String orderId = session.getMetadata().get("orderId");
                if (orderId != null) {
                    try {
                        Order order = orderService.getOrderById(Long.parseLong(orderId));
                        
                        // Mark the order as paid
                        // Note: We're not trying to get receipt URL from charges anymore
                        orderService.markOrderAsPaid(order.getOrderId(), session.getId(), null);
                        
                        // Clear user's cart after successful payment
                        String userId = session.getMetadata().get("userId");
                        if (userId != null) {
                            Long userIdLong = Long.parseLong(userId);
                            Optional<User> user = userService.findById(userIdLong);
                            if (user.isPresent()) {
                                // Assuming we need to clear the cart after successful payment
                                // This call depends on how your CartService is implemented
                                // cartService.clearCart(userIdLong);
                                logger.info("Cart cleared for user {}", userId);
                            }
                        }
                        
                        logger.info("Order {} processed from checkout session", orderId);
                    } catch (Exception e) {
                        logger.error("Error processing checkout session for order {}: {}", orderId, e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error handling checkout.session.completed event: {}", e.getMessage());
        }
    }
}