package com.ecommerce.inventory.dto;

import java.time.LocalDateTime;

public record InventoryResponse(

        String productId,

        String sku,

        Integer availableQuantity,

        Integer reservedQuantity,

        Integer totalQuantity,

        Long version,

        LocalDateTime updatedAt

) {
}