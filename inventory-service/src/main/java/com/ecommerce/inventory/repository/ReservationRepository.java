package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.entity.StockReservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository
        extends JpaRepository<StockReservation, String> {

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query(
            value = """
            INSERT INTO inventory_reservations
            (
                reservation_id,
                product_id,
                quantity,
                status,
                created_at,
                updated_at
            )
            VALUES
            (
                :reservationId,
                :productId,
                :quantity,
                'RESERVED',
                CURRENT_TIMESTAMP,
                CURRENT_TIMESTAMP
            )
            ON CONFLICT (reservation_id) DO NOTHING
            """,
            nativeQuery = true
    )
    int insertIfAbsent(
            @Param("reservationId") String reservationId,
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query(
            value = """
            UPDATE inventory_reservations
            SET status = 'RELEASED',
                updated_at = CURRENT_TIMESTAMP
            WHERE reservation_id = :reservationId
              AND status = 'RESERVED'
            """,
            nativeQuery = true
    )
    int markReleased(
            @Param("reservationId") String reservationId
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query(
            value = """
            UPDATE inventory_reservations
            SET status = 'CONFIRMED',
                updated_at = CURRENT_TIMESTAMP
            WHERE reservation_id = :reservationId
              AND status = 'RESERVED'
            """,
            nativeQuery = true
    )
    int markConfirmed(
            @Param("reservationId") String reservationId
    );
}