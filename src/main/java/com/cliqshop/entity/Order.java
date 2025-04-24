package com.cliqshop.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"orders", "addresses"}) 
    private User user;
    
    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();
    
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("order")
    private List<OrderItem> items = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "shipping_address_id", nullable = false)
    @JsonIgnoreProperties({"user"}) 
    private Address shippingAddress;
    
    @ManyToOne
    @JoinColumn(name = "billing_address_id")
    @JsonIgnoreProperties({"user"}) 
    private Address billingAddress;
    
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private PaymentDetails paymentDetails;
    
    // New payment-related fields
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "payment_intent_id")
    private String paymentIntentId;
    
    @Column(name = "payment_receipt_url")
    private String paymentReceiptUrl;
    
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;
    
    @Column(name = "shipping_method")
    private String shippingMethod;
    
    @Column(name = "order_notes")
    private String orderNotes;
    
    // Enum for order status
    public enum OrderStatus {
        PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED, PAYMENT_FAILED, REFUNDED
    }
    
    // Enum for payment status
    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }
    
    // Constructors
    public Order() {
    }
    
    public Order(User user, BigDecimal totalPrice, Address shippingAddress) {
        this.user = user;
        this.totalPrice = totalPrice;
        this.shippingAddress = shippingAddress;
    }
    
    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public List<OrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
    
    public Address getShippingAddress() {
        return shippingAddress;
    }
    
    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    public Address getBillingAddress() {
        return billingAddress;
    }
    
    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }
    
    public PaymentDetails getPaymentDetails() {
        return paymentDetails;
    }
    
    public void setPaymentDetails(PaymentDetails paymentDetails) {
        this.paymentDetails = paymentDetails;
    }
    
    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getPaymentIntentId() {
        return paymentIntentId;
    }
    
    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }
    
    public String getPaymentReceiptUrl() {
        return paymentReceiptUrl;
    }
    
    public void setPaymentReceiptUrl(String paymentReceiptUrl) {
        this.paymentReceiptUrl = paymentReceiptUrl;
    }
    
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public String getShippingMethod() {
        return shippingMethod;
    }
    
    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }
    
    public String getOrderNotes() {
        return orderNotes;
    }
    
    public void setOrderNotes(String orderNotes) {
        this.orderNotes = orderNotes;
    }
    
    // Helper methods
    public void calculateTotalPrice() {
        this.totalPrice = items.stream()
            .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Payment-related helper methods
    public void markAsPaid(String paymentIntentId, String receiptUrl) {
        this.paymentStatus = PaymentStatus.PAID;
        this.status = OrderStatus.PROCESSING;
        this.paymentIntentId = paymentIntentId;
        this.paymentReceiptUrl = receiptUrl;
        this.paymentDate = LocalDateTime.now();
    }
    
    public void markPaymentFailed(String paymentIntentId) {
        this.paymentStatus = PaymentStatus.FAILED;
        this.status = OrderStatus.PAYMENT_FAILED;
        this.paymentIntentId = paymentIntentId;
    }
    
    public void markAsRefunded(String paymentIntentId) {
        this.paymentStatus = PaymentStatus.REFUNDED;
        this.status = OrderStatus.REFUNDED;
        this.paymentIntentId = paymentIntentId;
    }
    
    public boolean isPaymentCompleted() {
        return PaymentStatus.PAID.equals(this.paymentStatus);
    }
}