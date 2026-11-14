package com.ecommerce.order.service;

import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderItemResponse;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.exception.InventoryIntegrationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderPersistenceService persistenceService;
    private final InventoryClient inventoryClient;

    public OrderService(
            OrderPersistenceService persistenceService,
            InventoryClient inventoryClient) {

        this.persistenceService = persistenceService;
        this.inventoryClient = inventoryClient;
    }

    public OrderResponse createOrder(CreateOrderRequest request) {

        log.info("create order: {}", request.toString());
        // Step 1: Persist order with PENDING status
        Order order = persistenceService.createPendingOrder(request);

        List<OrderItem> reservedItems = new ArrayList<>();

        try {

            // Step 2: Reserve stock for every item
            for (OrderItem item : order.getItems()) {

                inventoryClient.reserveStock(
                        order.getId(),
                        item.getProductId(),
                        item.getQuantity()
                );

                log.info("Reserved order item: {}", item.toString());
                // Record successful reservation
                reservedItems.add(item);
            }

            // Step 3: All reservations succeeded
            persistenceService.updateStatus(
                    order.getId(),
                    OrderStatus.CONFIRMED
            );

            log.info("Confirmed order : {}", order);

            order.setStatus(OrderStatus.CONFIRMED);

            return toResponse(order);

        } catch (RuntimeException reservationException) {

            // Step 4: Compensate successful reservations
            boolean compensationSucceeded =
                    compensateReservations(order, reservedItems);

            log.info("Error while order creation, compensationSucceeded: {}", compensationSucceeded);

            if (compensationSucceeded) {

                persistenceService.updateStatus(
                        order.getId(),
                        OrderStatus.FAILED
                );

                log.info("Order Composed Successfully : {}", order.toString());

                throw new InventoryIntegrationException(
                        "Order creation failed because stock reservation failed.",
                        reservationException
                );
            }

            log.info("Order need manual Compensation : {}", order.toString());

            persistenceService.updateStatus(
                    order.getId(),
                    OrderStatus.COMPENSATION_REQUIRED
            );

            throw new InventoryIntegrationException(
                    "Order creation failed. Some reservations may need manual or automatic compensation.",
                    reservationException
            );
        }
    }

    private boolean compensateReservations(
            Order order,
            List<OrderItem> reservedItems) {

        boolean allReleased = true;

        for (OrderItem item : reservedItems) {

            try {

                inventoryClient.releaseStock(
                        order.getId(),
                        item.getProductId(),
                        item.getQuantity()
                );

            } catch (RuntimeException releaseException) {

                allReleased = false;
            }
        }

        return allReleased;
    }

    public OrderResponse getOrder(Long orderId) {

        Order order = persistenceService.getOrder(orderId);

        return toResponse(order);
    }

    public List<OrderResponse> getAllOrders() {

        return persistenceService.getAllOrders()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<OrderResponse> getCustomerOrders(Long customerId) {

        return persistenceService.getCustomerOrders(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private OrderResponse toResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item -> new OrderItemResponse(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getLineTotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items
        );
    }

    public void cancelOrder(Long orderId) {

        Order order = persistenceService.getOrder(orderId);

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Only confirmed orders can be cancelled."
            );
        }

        boolean allReleased = compensateReservations(
                order,
                order.getItems()
        );

        if (!allReleased) {

            persistenceService.updateStatus(
                    orderId,
                    OrderStatus.COMPENSATION_REQUIRED
            );

            throw new InventoryIntegrationException(
                    "Order cancellation requires compensation."
            );
        }

        persistenceService.updateStatus(
                orderId,
                OrderStatus.CANCELLED
        );
    }
}