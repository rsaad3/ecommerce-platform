package com.ecommerce.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CreateOrderRequest(

        @NotNull
        @Positive
        Long customerId,

        @NotEmpty
        List<@Valid CreateOrderItemRequest> items

) {}