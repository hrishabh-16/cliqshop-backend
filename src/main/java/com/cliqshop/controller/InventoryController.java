package com.cliqshop.controller;

import com.cliqshop.entity.Inventory;
import com.cliqshop.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
//@PreAuthorize("hasRole('ADMIN')")
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Inventory> createInventory(@RequestBody Inventory inventory) {
        return ResponseEntity.ok(inventoryService.createInventory(inventory));
    }

    @PutMapping("/{productId}/stock")
    public ResponseEntity<Inventory> updateStock(
            @PathVariable Long productId,
            @RequestParam Integer change) {
        return ResponseEntity.ok(inventoryService.updateStock(productId, change));
    }

    @PutMapping("/{productId}/threshold")
    public ResponseEntity<Inventory> setLowStockThreshold(
            @PathVariable Long productId,
            @RequestParam Integer threshold) {
        return ResponseEntity.ok(inventoryService.setLowStockThreshold(productId, threshold));
    }

    @GetMapping("/low-stock")
    
    public ResponseEntity<List<Inventory>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    @PutMapping("/{productId}/location")
    
    public ResponseEntity<Inventory> updateWarehouseLocation(
            @PathVariable Long productId,
            @RequestParam String location) {
        return ResponseEntity.ok(inventoryService.updateWarehouseLocation(productId, location));
    }

    @DeleteMapping("/{inventoryId}")
  
    public ResponseEntity<Void> deleteInventory(@PathVariable Long inventoryId) {
        inventoryService.deleteInventory(inventoryId);
        return ResponseEntity.noContent().build();
    }
}