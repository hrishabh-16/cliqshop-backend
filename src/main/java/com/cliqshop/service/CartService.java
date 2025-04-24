package com.cliqshop.service;

import com.cliqshop.dto.CartDto;
import com.cliqshop.dto.CartItemDto;

import java.util.List;

public interface CartService {
    CartDto getCartByUserId(Long userId);
    CartDto addToCart(Long userId, Long productId, int quantity);
    CartDto removeCartItem(Long userId, Long productId);
    CartDto updateCartItemQuantity(Long userId, Long productId, int quantity);
    void clearCart(Long userId);
}