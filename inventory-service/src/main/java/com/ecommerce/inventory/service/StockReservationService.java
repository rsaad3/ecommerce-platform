package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.ReservationResponse;
import com.ecommerce.inventory.dto.ReservationResult;
import com.ecommerce.inventory.dto.ReserveStockRequest;

import com.ecommerce.inventory.entity.ReservationStatus;
import com.ecommerce.inventory.entity.StockReservation;

import com.ecommerce.inventory.exception.ConflictException;
import com.ecommerce.inventory.exception.InsufficientStockException;
import com.ecommerce.inventory.exception.ResourceNotFoundException;

import com.ecommerce.inventory.repository.InventoryRepository;
import com.ecommerce.inventory.repository.ReservationRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockReservationService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    StockReservationService.class
            );

    private final ReservationRepository reservationRepository;

    private final InventoryRepository inventoryRepository;

    public StockReservationService(
            ReservationRepository reservationRepository,
            InventoryRepository inventoryRepository) {

        this.reservationRepository = reservationRepository;
        this.inventoryRepository = inventoryRepository;
    }

    // =========================================
    // RESERVE STOCK
    // =========================================

    @Transactional
    public ReservationResult reserve(
            ReserveStockRequest request) {

        log.info(
                "Reserving stock. reservationId={}, productId={}, quantity={}",
                request.reservationId(),
                request.productId(),
                request.quantity()
        );

        // Step 1: Insert reservation if not already exists.

        int inserted = reservationRepository.insertIfAbsent(
                request.reservationId(),
                request.productId(),
                request.quantity()
        );

        // Step 2: Handle duplicate request.

        if (inserted == 0) {

            StockReservation existing =
                    getReservation(request.reservationId());

            validateSameRequest(existing, request);

            log.info(
                    "Reservation already exists: {}",
                    request.reservationId()
            );

            return new ReservationResult(
                    toResponse(existing),
                    false
            );
        }

        // Step 3: Atomically reserve stock.

        int updated = inventoryRepository.reserveStock(
                request.productId(),
                request.quantity()
        );

        if (updated == 0) {

            if (!inventoryRepository.existsByProductId(
                    request.productId())) {

                throw new ResourceNotFoundException(
                        "Product inventory not found: "
                                + request.productId()
                );
            }

            throw new InsufficientStockException(
                    "Insufficient stock for product: "
                            + request.productId()
            );
        }

        // Step 4: Retrieve created reservation.

        StockReservation reservation =
                getReservation(request.reservationId());

        log.info(
                "Stock reserved successfully. reservationId={}",
                request.reservationId()
        );

        return new ReservationResult(
                toResponse(reservation),
                true
        );
    }

    // =========================================
    // CONFIRM RESERVATION
    // =========================================

    @Transactional
    public ReservationResponse confirm(
            String reservationId) {

        StockReservation reservation =
                getReservation(reservationId);

        if (reservation.getStatus()
                == ReservationStatus.CONFIRMED) {

            return toResponse(reservation);
        }

        if (reservation.getStatus()
                == ReservationStatus.RELEASED) {

            throw new ConflictException(
                    "Released reservation cannot be confirmed"
            );
        }

        // Change reservation status only if RESERVED.

        int changed = reservationRepository.markConfirmed(
                reservationId
        );

        if (changed == 1) {

            // Consume the reserved quantity.

            int stockChanged =
                    inventoryRepository.confirmStock(
                            reservation.getProductId(),
                            reservation.getQuantity()
                    );

            if (stockChanged == 0) {

                throw new IllegalStateException(
                        "Inventory reservation is inconsistent"
                );
            }

            log.info(
                    "Reservation confirmed: {}",
                    reservationId
            );

            return toResponse(
                    getReservation(reservationId)
            );
        }

        // Another concurrent operation may have won.

        StockReservation latest =
                getReservation(reservationId);

        if (latest.getStatus()
                == ReservationStatus.CONFIRMED) {

            return toResponse(latest);
        }

        throw new ConflictException(
                "Reservation state changed before confirmation"
        );
    }

    // =========================================
    // RELEASE RESERVATION
    // =========================================

    @Transactional
    public ReservationResponse release(
            String reservationId) {

        StockReservation reservation =
                getReservation(reservationId);

        if (reservation.getStatus()
                == ReservationStatus.RELEASED) {

            return toResponse(reservation);
        }

        if (reservation.getStatus()
                == ReservationStatus.CONFIRMED) {

            throw new ConflictException(
                    "Confirmed reservation cannot be released"
            );
        }

        // Change status only if currently RESERVED.

        int changed = reservationRepository.markReleased(
                reservationId
        );

        if (changed == 1) {

            // Return quantity to available stock.

            int stockChanged =
                    inventoryRepository.releaseStock(
                            reservation.getProductId(),
                            reservation.getQuantity()
                    );

            if (stockChanged == 0) {

                throw new IllegalStateException(
                        "Inventory reservation is inconsistent"
                );
            }

            log.info(
                    "Reservation released: {}",
                    reservationId
            );

            return toResponse(
                    getReservation(reservationId)
            );
        }

        // Another concurrent operation may have won.

        StockReservation latest =
                getReservation(reservationId);

        if (latest.getStatus()
                == ReservationStatus.RELEASED) {

            return toResponse(latest);
        }

        throw new ConflictException(
                "Reservation state changed before release"
        );
    }

    // =========================================
    // PRIVATE METHODS
    // =========================================

    private StockReservation getReservation(
            String reservationId) {

        return reservationRepository
                .findById(reservationId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Reservation not found: "
                                        + reservationId
                        )
                );
    }

    private void validateSameRequest(
            StockReservation existing,
            ReserveStockRequest request) {

        boolean sameProduct =
                existing.getProductId().equals(
                        request.productId()
                );

        boolean sameQuantity =
                existing.getQuantity().equals(
                        request.quantity()
                );

        if (!sameProduct || !sameQuantity) {

            throw new ConflictException(
                    "Reservation ID already exists "
                            + "with different request data"
            );
        }
    }

    private ReservationResponse toResponse(
            StockReservation reservation) {

        return new ReservationResponse(
                reservation.getReservationId(),
                reservation.getProductId(),
                reservation.getQuantity(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt()
        );
    }
}