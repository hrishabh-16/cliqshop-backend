package com.cliqshop.controller;

import com.cliqshop.entity.Order;
import com.cliqshop.payment.PaymentService;
import com.cliqshop.payment.PaymentServiceImpl;
import com.cliqshop.service.OrderService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);
    
    @Autowired
    private PaymentServiceImpl paymentService;
    
    @Autowired
    private OrderService orderService;
    
    @Value("${stripe.webhook.secret}")
    private String webhookSecret;
    
    @PostMapping("/stripe")
    public ResponseEntity<?> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            logger.info("Received Stripe webhook");
            
            // Process the webhook event
            Event event = verifyAndConstructEvent(payload, sigHeader);
            
            // Use the PaymentServiceImpl to handle the event
            paymentService.handleWebhookEvent(event);
            
            logger.info("Webhook processed successfully: {}", event.getType());
            
            // Return a 200 response to acknowledge receipt of the event
            return ResponseEntity.ok().build();
        } catch (StripeException e) {
            logger.error("Error processing webhook: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    private Event verifyAndConstructEvent(String payload, String sigHeader) throws StripeException {
        return Webhook.constructEvent(payload, sigHeader, webhookSecret);
    }
}