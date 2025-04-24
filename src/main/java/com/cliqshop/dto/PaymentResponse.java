package com.cliqshop.dto;

public class PaymentResponse {
    private boolean success;
    private String clientSecret;
    private String paymentIntentId;
    private String status;
    private String message;

    // Default constructor
    public PaymentResponse() {
        this.success = false;
    }

    // Constructor for error responses
    public PaymentResponse(String errorMessage) {
        this.success = false;
        this.message = errorMessage;
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}