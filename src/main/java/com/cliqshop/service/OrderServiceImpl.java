package com.cliqshop.service;

import com.cliqshop.dto.OrderRequest;
import com.cliqshop.entity.*;
import com.cliqshop.exception.ResourceNotFoundException;
import com.cliqshop.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private AddressService addressService;

    @Override
    public Order placeOrder(OrderRequest orderRequest) {
        User user = userService.findById(orderRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + orderRequest.getUserId()));

        // Get billing and shipping addresses
        Address billingAddress = null;
        Address shippingAddress = null;

        if (orderRequest.getBillingAddressId() != null) {
            billingAddress = addressService.getAddressById(orderRequest.getBillingAddressId());
        }

        if (orderRequest.getShippingAddressId() != null) {
            shippingAddress = addressService.getAddressById(orderRequest.getShippingAddressId());
        } else if (orderRequest.getBillingAddressId() != null) {
            shippingAddress = billingAddress;
        }

        if (shippingAddress == null) {
            throw new IllegalArgumentException("Shipping address is required");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PENDING);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setTotalPrice(orderRequest.getTotalPrice());
        order.setBillingAddress(billingAddress);
        order.setShippingAddress(shippingAddress);
        order.setShippingMethod(orderRequest.getShippingMethod());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setOrderNotes(orderRequest.getOrderNotes());

        List<OrderItem> orderItems = orderRequest.getItems().stream()
                .map(item -> {
                    Product product = productService.findById(item.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProductId()));

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(product);
                    orderItem.setQuantity(item.getQuantity());
                    orderItem.setPrice(item.getPrice());
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);

        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUser_UserId(userId);
    }

    @Override
    public Page<Order> getOrdersByUserId(Long userId, int page, int size) {
        return orderRepository.findByUser_UserIdOrderByOrderDateDesc(userId, PageRequest.of(page, size));
    }

    @Override
    public long countOrdersByUserId(Long userId) {
        return orderRepository.countByUser_UserId(userId);
    }

    @Override
    public List<Order> getRecentOrdersByUserId(Long userId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "orderDate"));
        return orderRepository.findByUser_UserId(userId, pageRequest).getContent();
    }

    @Override
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override
    public boolean cancelOrder(Long orderId, Long userId) {
        Order order = getOrderById(orderId);

        if (userId != null && !order.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to the user");
        }

        if (order.getStatus() == Order.OrderStatus.PENDING ||
            order.getStatus() == Order.OrderStatus.PROCESSING) {
            order.setStatus(Order.OrderStatus.CANCELLED);
            orderRepository.save(order);
            return true;
        }

        return false;
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Order updateOrder(Order order) {
        if (order.getOrderId() == null) {
            throw new IllegalArgumentException("Order ID cannot be null for update");
        }

        getOrderById(order.getOrderId());

        return orderRepository.save(order);
    }

    @Override
    public Order updatePaymentStatus(Long orderId, Order.PaymentStatus paymentStatus, String paymentIntentId) {
        Order order = getOrderById(orderId);
        order.setPaymentStatus(paymentStatus);
        order.setPaymentIntentId(paymentIntentId);

        if (paymentStatus == Order.PaymentStatus.PAID) {
            order.setStatus(Order.OrderStatus.PROCESSING);
            order.setPaymentDate(LocalDateTime.now());
        } else if (paymentStatus == Order.PaymentStatus.FAILED) {
            order.setStatus(Order.OrderStatus.PAYMENT_FAILED);
        } else if (paymentStatus == Order.PaymentStatus.REFUNDED) {
            order.setStatus(Order.OrderStatus.REFUNDED);
        }

        logger.info("Updated payment status for order {}: {}", orderId, paymentStatus);
        return orderRepository.save(order);
    }

    @Override
    public Order markOrderAsPaid(Long orderId, String paymentIntentId, String receiptUrl) {
        Order order = getOrderById(orderId);
        order.markAsPaid(paymentIntentId, receiptUrl);
        logger.info("Marked order {} as paid with payment intent: {}", orderId, paymentIntentId);
        return orderRepository.save(order);
    }

    @Override
    public Order markOrderPaymentFailed(Long orderId, String paymentIntentId) {
        Order order = getOrderById(orderId);
        order.markPaymentFailed(paymentIntentId);
        logger.info("Marked order {} payment as failed with payment intent: {}", orderId, paymentIntentId);
        return orderRepository.save(order);
    }
}
