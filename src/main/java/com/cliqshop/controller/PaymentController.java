package com.cliqshop.controller;

import com.cliqshop.dto.PaymentIntentRequest;
import com.cliqshop.dto.PaymentResponse;
import com.cliqshop.entity.Order;
import com.cliqshop.payment.PaymentService;
import com.cliqshop.service.OrderService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private OrderService orderService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentIntentRequest request) {
        try {
            PaymentResponse response = paymentService.createPaymentIntent(request);
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Error creating payment intent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new PaymentResponse(e.getMessage()));
        }
    }

    /**
     * Create a Stripe Checkout Session for redirecting to Stripe's hosted payment page
     */
    @PostMapping("/create-checkout-session")
    public ResponseEntity<?> createCheckoutSession(@RequestBody CheckoutSessionRequest request) {
        try {
            logger.info("Creating checkout session for order: {}", request.getOrderId());
            
            // Validate the request
            if (request.getOrderId() == null || request.getAmount() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Order ID and amount are required"
                ));
            }
            
            // Get the order details
            Order order = orderService.getOrderById(request.getOrderId());
            if (order == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Order not found"
                ));
            }
            
            // Create the Stripe checkout session
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(request.getSuccessUrl())
                .setCancelUrl(request.getCancelUrl())
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(request.getCurrency())
                                .setUnitAmount((long) (request.getAmount().doubleValue() * 100)) // Convert to cents/paise
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Order #" + order.getOrderId())
                                        .setDescription("Purchase from CliqShop")
                                        .build()
                                )
                                .build()
                        )
                        .setQuantity(1L)
                        .build()
                );
            
            // Add customer email if available
            if (request.getCustomerEmail() != null && !request.getCustomerEmail().isEmpty()) {
                paramsBuilder.setCustomerEmail(request.getCustomerEmail());
            }
            
            // Add metadata for the order
            paramsBuilder.putMetadata("orderId", String.valueOf(request.getOrderId()));
            
            // Add userId to metadata if the order has a user
            if (order.getUser() != null && order.getUser().getUserId() != null) {
                paramsBuilder.putMetadata("userId", String.valueOf(order.getUser().getUserId()));
            }
            
            // Create the session
            Session session = paymentService.createCheckoutSession(paramsBuilder.build());
            
            // Update the order with the payment session id
            order.setPaymentIntentId(session.getId());
            order.setPaymentStatus(Order.PaymentStatus.PENDING);
            orderService.updateOrder(order);
            
            // Return the session details
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("sessionId", session.getId());
            responseData.put("url", session.getUrl());
            
            logger.info("Checkout session created: {}", session.getId());
            return ResponseEntity.ok(responseData);
            
        } catch (StripeException e) {
            logger.error("Stripe error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Payment processing error: " + e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Error creating checkout session: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "An unexpected error occurred"
            ));
        }
    }

    @PostMapping("/confirm-payment-intent")
    public ResponseEntity<?> confirmPaymentIntent(
            @RequestParam String paymentIntentId,
            @RequestParam String paymentMethodId) {
        try {
            PaymentIntent paymentIntent = paymentService.confirmPaymentIntent(paymentIntentId, paymentMethodId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paymentIntentId", paymentIntent.getId());
            response.put("status", paymentIntent.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Error confirming payment intent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PostMapping("/cancel-payment-intent")
    public ResponseEntity<?> cancelPaymentIntent(@RequestParam String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = paymentService.cancelPaymentIntent(paymentIntentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paymentIntentId", paymentIntent.getId());
            response.put("status", paymentIntent.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Error canceling payment intent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/payment-intent/{paymentIntentId}")
    public ResponseEntity<?> getPaymentIntent(@PathVariable String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = paymentService.retrievePaymentIntent(paymentIntentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("paymentIntentId", paymentIntent.getId());
            response.put("status", paymentIntent.getStatus());
            response.put("amount", paymentIntent.getAmount());
            response.put("currency", paymentIntent.getCurrency());
            
            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            logger.error("Error retrieving payment intent: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @GetMapping("/config")
    public ResponseEntity<?> getStripeConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("publishableKey", paymentService.getPublishableKey());
            return ResponseEntity.ok(config);
        } catch (Exception e) {
            logger.error("Error fetching Stripe config: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    // Request class for Checkout Session
    public static class CheckoutSessionRequest {
        private Long orderId;
        private String customerEmail;
        private String customerName;
        private BigDecimal amount;
        private String currency;
        private String successUrl;
        private String cancelUrl;

        // Getters and setters
        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getCustomerEmail() {
            return customerEmail;
        }

        public void setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getSuccessUrl() {
            return successUrl;
        }

        public void setSuccessUrl(String successUrl) {
            this.successUrl = successUrl;
        }

        public String getCancelUrl() {
            return cancelUrl;
        }

        public void setCancelUrl(String cancelUrl) {
            this.cancelUrl = cancelUrl;
        }
    }
}