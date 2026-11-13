package com.ecommerce.inventory.entity;

import jakarta.persistence.*;

import jakarta.persistence.Table;
import org.hibernate.annotations.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "inventory_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_inventory_product",
                        columnNames = "product_id"
                ),
                @UniqueConstraint(
                        name = "uk_inventory_sku",
                        columnNames = "sku"
                )
        }
)
@Check(
        constraints =
                "available_quantity >= 0 AND reserved_quantity >= 0"
)
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "product_id",
            nullable = false,
            length = 100
    )
    private String productId;

    @Column(
            name = "sku",
            nullable = false,
            length = 100
    )
    private String sku;

    @Column(
            name = "available_quantity",
            nullable = false
    )
    private Integer availableQuantity;

    @Column(
            name = "reserved_quantity",
            nullable = false
    )
    private Integer reservedQuantity;

    @Version
    @Column(
            name = "version",
            nullable = false
    )
    private Long version;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public InventoryItem() {
    }

    public InventoryItem(
            String productId,
            String sku,
            Integer availableQuantity) {

        this.productId = productId;
        this.sku = sku;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
    }

    @PrePersist
    protected void onCreate() {

        this.updatedAt = LocalDateTime.now();

        if (this.version == null) {
            this.version = 0L;
        }
    }

    @PreUpdate
    protected void onUpdate() {

        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getSku() {
        return sku;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }

    public Long getVersion() {
        return version;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}