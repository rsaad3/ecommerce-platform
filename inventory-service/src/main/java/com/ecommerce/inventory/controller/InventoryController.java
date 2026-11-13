package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.CreateInventoryItemRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.RestockRequest;

import com.ecommerce.inventory.service.InventoryManagementService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryManagementService inventoryService;

    public InventoryController(
            InventoryManagementService inventoryService) {

        this.inventoryService = inventoryService;
    }

    @PostMapping("/items")
    public ResponseEntity<InventoryResponse> createItem(
            @Valid @RequestBody CreateInventoryItemRequest request) {

        InventoryResponse response =
                inventoryService.createItem(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/inventory/items/"
                                        + response.productId()
                        )
                )
                .body(response);
    }

    @GetMapping("/items/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(
            @PathVariable String productId) {

        return ResponseEntity.ok(
                inventoryService.getInventory(productId)
        );
    }

    @PostMapping("/items/{productId}/restock")
    public ResponseEntity<InventoryResponse> restock(
            @PathVariable String productId,
            @Valid @RequestBody RestockRequest request) {

        return ResponseEntity.ok(
                inventoryService.restock(
                        productId,
                        request
                )
        );
    }
}