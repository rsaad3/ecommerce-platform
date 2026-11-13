package com.ecommerce.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ReserveStockRequest(

        @NotBlank
        @Size(max = 100)
        String reservationId,

        @NotBlank
        @Size(max = 100)
        String productId,

        @NotNull
        @Positive
        Integer quantity

) {
}