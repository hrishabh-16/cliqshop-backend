package com.cliqshop.payment;

import com.cliqshop.dto.PaymentIntentRequest;
import com.cliqshop.dto.PaymentResponse;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.billingportal.Session;
import com.stripe.param.billingportal.SessionCreateParams;

public interface PaymentService {
    
    /**
     * Creates a payment intent with Stripe
     * 
     * @param request PaymentIntentRequest containing amount, currency, etc.
     * @return PaymentResponse with client secret and status
     * @throws StripeException if Stripe API calls fail
     */
	public PaymentResponse createPaymentIntent(PaymentIntentRequest request) throws StripeException;
    
    /**
     * Confirms a payment intent with Stripe
     * 
     * @param paymentIntentId ID of the payment intent to confirm
     * @param paymentMethodId ID of the payment method to use
     * @return The confirmed PaymentIntent object
     * @throws StripeException if Stripe API calls fail
     */
    PaymentIntent confirmPaymentIntent(String paymentIntentId, String paymentMethodId) throws StripeException;
    
    /**
     * Cancels a payment intent with Stripe
     * 
     * @param paymentIntentId ID of the payment intent to cancel
     * @return The canceled PaymentIntent object
     * @throws StripeException if Stripe API calls fail
     */
    PaymentIntent cancelPaymentIntent(String paymentIntentId) throws StripeException;
    
    /**
     * Retrieves a payment intent from Stripe
     * 
     * @param paymentIntentId ID of the payment intent to retrieve
     * @return The PaymentIntent object
     * @throws StripeException if Stripe API calls fail
     */
    PaymentIntent retrievePaymentIntent(String paymentIntentId) throws StripeException;
    
    /**
     * Processes a webhook event from Stripe
     * 
     * @param payload The raw JSON payload from Stripe
     * @param sigHeader The Stripe-Signature header
     * @return The processed Event object
     * @throws StripeException if Stripe API calls fail
     */
    Event processWebhookEvent(String payload, String sigHeader) throws StripeException;
    public com.stripe.model.checkout.Session createCheckoutSession(com.stripe.param.checkout.SessionCreateParams sessionCreateParams) throws StripeException;

    String getPublishableKey(); 
}