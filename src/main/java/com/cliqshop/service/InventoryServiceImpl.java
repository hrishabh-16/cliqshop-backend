package com.cliqshop.service;

import com.cliqshop.entity.Inventory;
import com.cliqshop.entity.Product;
import com.cliqshop.exception.ResourceNotFoundException;
import com.cliqshop.repository.InventoryRepository;
import com.cliqshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product ID: " + productId));
    }
    
    @Override
    public Inventory updateStock(Long productId, Integer quantityChange) {
        Inventory inventory = getInventoryByProductId(productId);
        int newQuantity = inventory.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Stock level cannot be negative");
        }
        inventory.setQuantity(newQuantity);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory setLowStockThreshold(Long productId, Integer threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold cannot be negative");
        }
        Inventory inventory = getInventoryByProductId(productId);
        inventory.setLowStockThreshold(threshold);
        return inventoryRepository.save(inventory);
    }

    @Override
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    @Override
    public Inventory updateWarehouseLocation(Long productId, String location) {
        Inventory inventory = getInventoryByProductId(productId);
        inventory.setWarehouseLocation(location);
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory createInventory(Inventory inventory) {
        if (inventory.getProduct() == null || inventory.getProduct().getProductId() == null) {
            throw new IllegalArgumentException("Product must be specified with a valid ID");
        }
        
        Product product = productRepository.findById(inventory.getProduct().getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + inventory.getProduct().getProductId()));
        
        if (inventoryRepository.existsByProduct(product)) {
            throw new IllegalArgumentException("Inventory already exists for product ID: " + product.getProductId());
        }
        
        inventory.setProduct(product);
        inventory.setLastUpdated(LocalDateTime.now());
        if (inventory.getLastRestocked() == null) {
            inventory.setLastRestocked(LocalDateTime.now());
        }
        
        return inventoryRepository.save(inventory);
    }

    @Override
    public void deleteInventory(Long inventoryId) {
        if (!inventoryRepository.existsById(inventoryId)) {
            throw new ResourceNotFoundException("Inventory not found with ID: " + inventoryId);
        }
        inventoryRepository.deleteById(inventoryId);
    }
}