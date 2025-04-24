package com.cliqshop.service;

import com.cliqshop.entity.Inventory;
import java.util.List;

public interface InventoryService {
    List<Inventory> getAllInventory();
    Inventory getInventoryByProductId(Long productId);
    Inventory updateStock(Long productId, Integer quantityChange);
    Inventory setLowStockThreshold(Long productId, Integer threshold);
    List<Inventory> getLowStockItems();
    Inventory updateWarehouseLocation(Long productId, String location);
    Inventory createInventory(Inventory inventory);
    void deleteInventory(Long inventoryId);
}