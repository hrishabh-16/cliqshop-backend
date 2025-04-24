package com.cliqshop.service;

import com.cliqshop.dto.OrderRequest;
import com.cliqshop.entity.Order;
import com.cliqshop.entity.Order.OrderStatus;
import com.cliqshop.entity.Order.PaymentStatus;

import java.util.List;

import org.springframework.data.domain.Page;

public interface OrderService {
    Order placeOrder(OrderRequest orderRequest);
    Order getOrderById(Long orderId);
    List<Order> getAllOrders();
    List<Order> getOrdersByUserId(Long userId);
    Order updateOrderStatus(Long orderId, OrderStatus status);
    boolean cancelOrder(Long orderId, Long userId);
    Order saveOrder(Order order);
    public Page<Order> getOrdersByUserId(Long userId, int page, int size);
    List<Order> getRecentOrdersByUserId(Long userId, int limit);
    public long countOrdersByUserId(Long userId);
    /**
     * Update an order object and save it to the database
     *
     * @param order The updated order object
     * @return The updated order
     */
    Order updateOrder(Order order);
    
    /**
     * Update the payment status of an order
     *
     * @param orderId The ID of the order to update
     * @param paymentStatus The new payment status
     * @param paymentIntentId The payment intent ID from Stripe
     * @return The updated order
     */
    Order updatePaymentStatus(Long orderId, PaymentStatus paymentStatus, String paymentIntentId);
    
    /**
     * Mark an order as paid
     *
     * @param orderId The ID of the order to update
     * @param paymentIntentId The payment intent ID from Stripe
     * @param receiptUrl The URL to the payment receipt
     * @return The updated order
     */
    Order markOrderAsPaid(Long orderId, String paymentIntentId, String receiptUrl);
    
    /**
     * Mark an order payment as failed
     *
     * @param orderId The ID of the order to update
     * @param paymentIntentId The payment intent ID from Stripe
     * @return The updated order
     */
    Order markOrderPaymentFailed(Long orderId, String paymentIntentId);
    
    
}