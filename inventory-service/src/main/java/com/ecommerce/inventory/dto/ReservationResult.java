package com.ecommerce.inventory.dto;

public record ReservationResult(

        ReservationResponse reservation,

        boolean created

) {
}