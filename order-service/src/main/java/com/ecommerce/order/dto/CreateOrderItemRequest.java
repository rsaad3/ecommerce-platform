package com.ecommerce.order.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CreateOrderItemRequest(

        @NotNull
        @Positive
        String productId,

        @NotNull
        @Positive
        Integer quantity,

        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 17, fraction = 2)
        BigDecimal unitPrice

) {}