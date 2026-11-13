package com.ecommerce.inventory.entity;

import jakarta.persistence.*;

import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_reservations")
@Check(constraints = "quantity > 0")
public class StockReservation {

    @Id
    @Column(
            name = "reservation_id",
            length = 100
    )
    private String reservationId;

    @Column(
            name = "product_id",
            nullable = false,
            length = 100
    )
    private String productId;

    @Column(
            name = "quantity",
            nullable = false
    )
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "status",
            nullable = false,
            length = 20
    )
    private ReservationStatus status;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "updated_at",
            nullable = false
    )
    private LocalDateTime updatedAt;

    public StockReservation() {
    }

    public StockReservation(
            String reservationId,
            String productId,
            Integer quantity,
            ReservationStatus status) {

        this.reservationId = reservationId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}