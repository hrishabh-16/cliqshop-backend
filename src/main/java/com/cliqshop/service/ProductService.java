package com.cliqshop.service;

import com.cliqshop.dto.ProductDto;
import com.cliqshop.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Optional<Product> findById(Long id); 
    List<Product> getProductsByCategory(Long categoryId);
    List<Product> searchProductsByName(String name);
    Product createProduct(ProductDto productDto);
    Product updateProduct(Long id, ProductDto productDto);
    void deleteProduct(Long id);
}