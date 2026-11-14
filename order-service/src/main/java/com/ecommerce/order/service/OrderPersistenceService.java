package com.ecommerce.order.service;

import com.ecommerce.common.exception.ResourceNotFoundException;
import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.CreateOrderItemRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class OrderPersistenceService {

    private final OrderRepository orderRepository;

    public OrderPersistenceService(
            OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createPendingOrder(CreateOrderRequest request) {

        Order order = new Order();

        order.setCustomerId(request.customerId());
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.items()) {

            BigDecimal lineTotal = itemRequest.unitPrice()
                    .multiply(BigDecimal.valueOf(
                            itemRequest.quantity()
                    ));

            OrderItem item = new OrderItem();

            item.setProductId(itemRequest.productId());
            item.setQuantity(itemRequest.quantity());
            item.setUnitPrice(itemRequest.unitPrice());
            item.setLineTotal(lineTotal);

            order.addItem(item);

            total = total.add(lineTotal);
        }

        order.setTotalAmount(total);

        return orderRepository.save(order);
    }

    @Transactional
    public void updateStatus(
            Long orderId,
            OrderStatus newStatus) {

        Order order = findOrder(orderId);

        order.setStatus(newStatus);
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long orderId) {
        return findOrder(orderId);
    }

    @Transactional(readOnly = true)
    public java.util.List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public java.util.List<Order> getCustomerOrders(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    private Order findOrder(Long orderId) {

        return orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found: " + orderId
                        )
                );
    }
}