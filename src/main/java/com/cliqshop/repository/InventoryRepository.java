package com.cliqshop.repository;

import com.cliqshop.entity.Inventory;
import com.cliqshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    // Use explicit query since field is productId not id
    @Query("SELECT i FROM Inventory i WHERE i.product.productId = :productId")
    Optional<Inventory> findByProductId(Long productId);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.lowStockThreshold")
    List<Inventory> findLowStockItems();
    
    boolean existsByProduct(Product product);
    Optional<Inventory> findBySku(String sku);
    List<Inventory> findByWarehouseLocation(String location);
}