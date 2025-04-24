package com.cliqshop.dto;

import java.math.BigDecimal;

public class PaymentIntentRequest {
    private Long orderId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private String email;
    private String paymentMethodType;

    // Default constructor
    public PaymentIntentRequest() {
    }

    // Parameterized constructor
    public PaymentIntentRequest(Long orderId, BigDecimal amount, String currency, String description, String email) {
        this.orderId = orderId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.email = email;
    }

    // Getters and setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }
}