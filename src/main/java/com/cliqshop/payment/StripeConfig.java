package com.cliqshop.payment;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    @Value("${stripe.api.secretKey}")
    private String stripeApiKey;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Bean
    public String stripeApiKey() {
        // Initialize Stripe API with the secret key
        Stripe.apiKey = stripeApiKey;
        return stripeApiKey;
    }

    @Bean
    public String stripeWebhookSecret() {
        return webhookSecret;
    }
}