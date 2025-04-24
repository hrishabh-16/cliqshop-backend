package com.cliqshop.service;

import com.cliqshop.dto.CartDto;
import com.cliqshop.dto.CartItemDto;
import com.cliqshop.entity.*;
import com.cliqshop.exception.ResourceNotFoundException;
import com.cliqshop.repository.CartRepository;
import com.cliqshop.repository.ProductRepository;
import com.cliqshop.repository.UserRepository;
import com.cliqshop.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public CartDto getCartByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        return convertToDto(cart);
    }

    @Override
    @Transactional
    public CartDto addToCart(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart(user);
                    return cartRepository.save(newCart);
                });

        // Check if product already exists in cart
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem newItem = new CartItem(cart, product, quantity);
            cart.getItems().add(newItem);
        }

        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return convertToDto(cart);
    }

    @Override
    @Transactional
    public CartDto removeCartItem(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        cart.getItems().removeIf(item -> item.getProduct().getProductId().equals(productId));
        cart.calculateTotalPrice();
        cartRepository.save(cart);

        return convertToDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateCartItemQuantity(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    cart.calculateTotalPrice();
                    cartRepository.save(cart);
                });

        return convertToDto(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user id: " + userId));

        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private CartDto convertToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setCartId(cart.getCartId());
        cartDto.setUserId(cart.getUser().getUserId());
        cartDto.setTotalPrice(cart.getTotalPrice());

        List<CartItemDto> itemDtos = cart.getItems().stream()
                .map(this::convertToItemDto)
                .collect(Collectors.toList());

        cartDto.setItems(itemDtos);
        return cartDto;
    }

    private CartItemDto convertToItemDto(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setCartItemId(item.getCartItemId());
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getName());
        dto.setProductPrice(item.getProduct().getPrice());
        dto.setProductImageUrl(item.getProduct().getImageUrl());
        dto.setQuantity(item.getQuantity());
        dto.setSubTotal(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        return dto;
    }
}