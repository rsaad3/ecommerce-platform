package com.ecommerce.order.exception;

public class InventoryIntegrationException
        extends RuntimeException {

    public InventoryIntegrationException(String message) {
        super(message);
    }

    public InventoryIntegrationException(
            String message,
            Throwable cause) {
        super(message, cause);
    }
}