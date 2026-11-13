package com.ecommerce.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record CreateInventoryItemRequest(

        @NotBlank
        @Size(max = 100)
        String productId,

        @NotBlank
        @Size(max = 100)
        String sku,

        @NotNull
        @PositiveOrZero
        Integer availableQuantity

) {
}