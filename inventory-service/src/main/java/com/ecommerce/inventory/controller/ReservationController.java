package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.ReservationResponse;
import com.ecommerce.inventory.dto.ReservationResult;
import com.ecommerce.inventory.dto.ReserveStockRequest;

import com.ecommerce.inventory.service.StockReservationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/reservations")
public class ReservationController {

    private final StockReservationService reservationService;

    public ReservationController(
            StockReservationService reservationService) {

        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reserve(
            @Valid @RequestBody ReserveStockRequest request) {

        ReservationResult result =
                reservationService.reserve(request);

        if (result.created()) {

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(result.reservation());
        }

        return ResponseEntity
                .ok(result.reservation());
    }

    @PostMapping("/{reservationId}/confirm")
    public ResponseEntity<ReservationResponse> confirm(
            @PathVariable String reservationId) {

        return ResponseEntity.ok(
                reservationService.confirm(reservationId)
        );
    }

    @PostMapping("/{reservationId}/release")
    public ResponseEntity<ReservationResponse> release(
            @PathVariable String reservationId) {

        return ResponseEntity.ok(
                reservationService.release(reservationId)
        );
    }
}