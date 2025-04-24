package com.cliqshop.controller;

import com.cliqshop.dto.CategoryDto;
import com.cliqshop.dto.ProductDto;
import com.cliqshop.entity.Category;
import com.cliqshop.entity.Inventory;
import com.cliqshop.entity.Order;
import com.cliqshop.entity.Product;
import com.cliqshop.entity.User;
import com.cliqshop.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InventoryService inventoryService;

    // Dashboard Endpoints
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        return ResponseEntity.ok().body(
            new Object() {
                public final long totalProducts = productService.getAllProducts().size();
                public final long totalUsers = userService.getTotalUsers();
                public final long totalCategories = categoryService.getAllCategories().size();
                public final long totalOrders = orderService.getAllOrders().size();
                public final long lowStockItems = inventoryService.getLowStockItems().size();
            }
        );
    }

    @GetMapping("/dashboard/recent-products")
    public ResponseEntity<List<Product>> getRecentProducts(@RequestParam(defaultValue = "5") int count) {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products.subList(0, Math.min(count, products.size())));
    }

    @GetMapping("/dashboard/recent-categories")
    public ResponseEntity<List<Category>> getRecentCategories(@RequestParam(defaultValue = "5") int count) {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories.subList(0, Math.min(count, categories.size())));
    }

    @GetMapping("/dashboard/low-stock-items")
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    // Product Management
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.createProduct(productDto));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // Category Management
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDto));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // Order Management
    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestParam Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }

    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id) {
        boolean cancelled = orderService.cancelOrder(id, null); // Admin can cancel any order
        if (cancelled) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().body("Failed to cancel order");
    }

    // Inventory Management
    @GetMapping("/inventory")
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/inventory/product/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PutMapping("/inventory/{productId}/stock")
    public ResponseEntity<Inventory> updateStock(@PathVariable Long productId, @RequestParam Integer change) {
        return ResponseEntity.ok(inventoryService.updateStock(productId, change));
    }

    @PutMapping("/inventory/{productId}/threshold")
    public ResponseEntity<Inventory> setLowStockThreshold(@PathVariable Long productId, @RequestParam Integer threshold) {
        return ResponseEntity.ok(inventoryService.setLowStockThreshold(productId, threshold));
    }

    @PutMapping("/inventory/{productId}/location")
    public ResponseEntity<Inventory> updateWarehouseLocation(@PathVariable Long productId, @RequestParam String location) {
        return ResponseEntity.ok(inventoryService.updateWarehouseLocation(productId, location));
    }

    @PostMapping("/inventory")
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.createInventory(inventory));
    }

    @DeleteMapping("/inventory/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}