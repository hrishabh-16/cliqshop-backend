package com.cliqshop.repository;

import com.cliqshop.entity.Order;
import com.cliqshop.entity.Order.OrderStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Change from findByUser_Id to findByUser_UserId
    List<Order> findByUser_UserId(Long userId);
    
    List<Order> findByStatus(OrderStatus status);
    
    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);
    
    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.user.userId = :userId")
    List<Order> findByUserWithItems(@Param("userId") Long userId);
    
    @Query("SELECT o FROM Order o JOIN FETCH o.paymentDetails WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithPaymentDetails(@Param("orderId") Long orderId);
    
 // FIX: Use UserId, not Id
    Page<Order> findByUser_UserIdOrderByOrderDateDesc(Long userId, Pageable pageable);
    Page<Order> findByUser_UserId(Long userId, Pageable pageable);
    long countByUser_UserId(Long userId);
}
