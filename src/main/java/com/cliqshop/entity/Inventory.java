package com.cliqshop.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName = "productId" ,nullable = false, unique = true)
    @JsonIgnoreProperties("inventory")
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "low_stock_threshold")
    private Integer lowStockThreshold;

    @Column(name = "last_restocked")
    private LocalDateTime lastRestocked;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "warehouse_location")
    private String warehouseLocation;

    @Column(name = "sku", unique = true)
    private String sku;

    // Constructors
    public Inventory() {
    }

    public Inventory(Product product, Integer quantity, Integer lowStockThreshold, 
                    LocalDateTime lastRestocked, String warehouseLocation, String sku) {
        this.product = product;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
        this.lastRestocked = lastRestocked;
        this.lastUpdated = LocalDateTime.now();
        this.warehouseLocation = warehouseLocation;
        this.sku = sku;
    }

    // Getters and Setters
   

    public Product getProduct() {
        return product;
    }

    public Long getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Long inventoryId) {
		this.inventoryId = inventoryId;
	}

	public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.lastUpdated = LocalDateTime.now();
    }

    public Integer getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(Integer lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

    public LocalDateTime getLastRestocked() {
        return lastRestocked;
    }

    public void setLastRestocked(LocalDateTime lastRestocked) {
        this.lastRestocked = lastRestocked;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    // Business methods
    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    public void restock(Integer amount) {
        this.quantity += amount;
        this.lastRestocked = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }
}
