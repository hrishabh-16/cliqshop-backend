package com.cliqshop.controller;

import com.cliqshop.entity.Order;
import com.cliqshop.entity.Order.OrderStatus;
import com.cliqshop.entity.Product;
import com.cliqshop.service.OrderService;
import com.cliqshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final OrderService orderService;
    private final ProductService productService;
    private static final int LOW_STOCK_THRESHOLD = 10;

    @Autowired
    public ReportController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getCombinedReport() {
        Map<String, Object> response = new HashMap<>();
        
        // Sales Report Data
        List<Order> orders = orderService.getAllOrders();
        Map<String, Object> salesReport = generateSalesReport(orders);
        
        // Inventory Report Data
        List<Product> products = productService.getAllProducts();
        Map<String, Object> inventoryReport = generateInventoryReport(products);
        
        response.put("sales", salesReport);
        response.put("inventory", inventoryReport);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesReport() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(generateSalesReport(orders));
    }

    @GetMapping("/inventory")
    public ResponseEntity<Map<String, Object>> getInventoryReport() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(generateInventoryReport(products));
    }

    private Map<String, Object> generateSalesReport(List<Order> orders) {
        Map<String, Object> report = new HashMap<>();
        
        int totalOrders = orders.size();
        BigDecimal totalRevenue = orders.stream()
                .map(order -> order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgOrderValue = totalOrders > 0 ? 
                totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : 
                BigDecimal.ZERO;

        Map<OrderStatus, Long> statusCount = orders.stream()
                .collect(Collectors.groupingBy(
                        Order::getStatus,
                        Collectors.counting()
                ));
        
        // Convert OrderStatus enum to String keys
        Map<String, Long> statusDistribution = statusCount.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().name(),
                        Map.Entry::getValue
                ));

        report.put("totalOrders", totalOrders);
        report.put("totalRevenue", totalRevenue);
        report.put("avgOrderValue", avgOrderValue);
        report.put("statusDistribution", statusDistribution);

        return report;
    }

    private Map<String, Object> generateInventoryReport(List<Product> products) {
        Map<String, Object> report = new HashMap<>();
        
        BigDecimal totalValue = products.stream()
                .map(p -> {
                    BigDecimal price = p.getPrice() != null ? p.getPrice() : BigDecimal.ZERO;
                    int quantity = p.getInventory() != null ? p.getInventory().getQuantity() : 0;
                    return price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long lowStockItems = products.stream()
                .filter(p -> {
                    Integer quantity = p.getInventory() != null ? p.getInventory().getQuantity() : 0;
                    return quantity < LOW_STOCK_THRESHOLD;
                })
                .count();

        report.put("totalProducts", products.size());
        report.put("totalValue", totalValue);
        report.put("lowStockItems", lowStockItems);
        report.put("lowStockThreshold", LOW_STOCK_THRESHOLD);

        return report;
    }
}