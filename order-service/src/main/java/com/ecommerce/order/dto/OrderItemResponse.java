package com.ecommerce.order.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productId,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}