package com.cliqshop.controller;

import com.cliqshop.dto.CartDto;
import com.cliqshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<CartDto> getCart(@PathVariable Long userId) {
        CartDto cart = cartService.getCartByUserId(userId);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<CartDto> addToCart(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        CartDto cart = cartService.addToCart(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{userId}/remove")
    public ResponseEntity<CartDto> removeFromCart(
            @PathVariable Long userId,
            @RequestParam Long productId) {
        CartDto cart = cartService.removeCartItem(userId, productId);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<CartDto> updateCartItem(
            @PathVariable Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity) {
        CartDto cart = cartService.updateCartItemQuantity(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}