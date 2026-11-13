package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.CreateInventoryItemRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.RestockRequest;

import com.ecommerce.inventory.entity.InventoryItem;

import com.ecommerce.inventory.exception.ConflictException;
import com.ecommerce.inventory.exception.ResourceNotFoundException;

import com.ecommerce.inventory.repository.InventoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryManagementService {

    private final InventoryRepository inventoryRepository;

    public InventoryManagementService(
            InventoryRepository inventoryRepository) {

        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public InventoryResponse createItem(
            CreateInventoryItemRequest request) {

        if (inventoryRepository.existsByProductId(
                request.productId())) {

            throw new ConflictException(
                    "Product inventory already exists"
            );
        }

        if (inventoryRepository.existsBySku(request.sku())) {

            throw new ConflictException(
                    "SKU already exists"
            );
        }

        InventoryItem item = new InventoryItem(
                request.productId(),
                request.sku(),
                request.availableQuantity()
        );

        InventoryItem saved =
                inventoryRepository.save(item);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getInventory(
            String productId) {

        InventoryItem item = inventoryRepository
                .findByProductId(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Inventory not found for product: "
                                        + productId
                        )
                );

        return toResponse(item);
    }

    @Transactional
    public InventoryResponse restock(
            String productId,
            RestockRequest request) {

        int updated = inventoryRepository.restock(
                productId,
                request.quantity()
        );

        if (updated == 0) {

            throw new ResourceNotFoundException(
                    "Inventory not found for product: "
                            + productId
            );
        }

        return getInventory(productId);
    }

    private InventoryResponse toResponse(
            InventoryItem item) {

        int totalQuantity =
                item.getAvailableQuantity()
                        + item.getReservedQuantity();

        return new InventoryResponse(
                item.getProductId(),
                item.getSku(),
                item.getAvailableQuantity(),
                item.getReservedQuantity(),
                totalQuantity,
                item.getVersion(),
                item.getUpdatedAt()
        );
    }
}