package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.entity.InventoryItem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository
        extends JpaRepository<InventoryItem, Long> {

    Optional<InventoryItem> findByProductId(
            String productId
    );

    boolean existsByProductId(String productId);

    boolean existsBySku(String sku);

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query(
            value = """
                    UPDATE inventory_items
                    SET available_quantity =
                            available_quantity - :quantity,

                        reserved_quantity =
                            reserved_quantity + :quantity,

                        version = version + 1,

                        updated_at = CURRENT_TIMESTAMP

                    WHERE product_id = :productId
                      AND available_quantity >= :quantity
                    """,
            nativeQuery = true
    )
    int reserveStock(
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query(
            value = """
                    UPDATE inventory_items
                    SET available_quantity =
                            available_quantity + :quantity,

                        version = version + 1,

                        updated_at = CURRENT_TIMESTAMP

                    WHERE product_id = :productId
                    """,
            nativeQuery = true
    )
    int restock(
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query(
            value = """
                    UPDATE inventory_items
                    SET available_quantity =
                            available_quantity + :quantity,

                        reserved_quantity =
                            reserved_quantity - :quantity,

                        version = version + 1,

                        updated_at = CURRENT_TIMESTAMP

                    WHERE product_id = :productId
                      AND reserved_quantity >= :quantity
                    """,
            nativeQuery = true
    )
    int releaseStock(
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );

    @Modifying(
            clearAutomatically = true,
            flushAutomatically = true
    )
    @Query(
            value = """
                    UPDATE inventory_items
                    SET reserved_quantity =
                            reserved_quantity - :quantity,

                        version = version + 1,

                        updated_at = CURRENT_TIMESTAMP

                    WHERE product_id = :productId
                      AND reserved_quantity >= :quantity
                    """,
            nativeQuery = true
    )
    int confirmStock(
            @Param("productId") String productId,
            @Param("quantity") Integer quantity
    );
}