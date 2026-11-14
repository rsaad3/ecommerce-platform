package com.ecommerce.order.client;

import com.ecommerce.order.exception.InventoryIntegrationException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(RestClient inventoryRestClient) {
        this.restClient = inventoryRestClient;
    }

    public void reserveStock(
            Long orderId,
            String productId,
            Integer quantity) {

        sendStockRequest(
                "/api/inventory/reservations",
                orderId,
                productId,
                quantity
        );
    }

    public void releaseStock(
            Long orderId,
            String productId,
            Integer quantity) {

        sendStockRequest(
                "/api/inventory/reservations/release",
                orderId,
                productId,
                quantity
        );
    }

    private void sendStockRequest(
            String endpoint,
            Long orderId,
            String productId,
            Integer quantity) {

        try {
            restClient.post()
                    .uri(endpoint)
                    .body(new StockRequest(
                            orderId,
                            orderId+"_"+productId,
                            String.valueOf(productId),
                            quantity
                    ))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientException ex) {
            throw new InventoryIntegrationException(
                    "Inventory service call failed: " + endpoint,
                    ex
            );
        }
    }

    public record StockRequest(
            Long orderId,
            String reservationId,
            String productId,
            Integer quantity
    ) {}
}