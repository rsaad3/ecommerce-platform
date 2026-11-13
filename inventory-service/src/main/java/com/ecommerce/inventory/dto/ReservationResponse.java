package com.ecommerce.inventory.dto;

import com.ecommerce.inventory.entity.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(

        String reservationId,

        String productId,

        Integer quantity,

        ReservationStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}