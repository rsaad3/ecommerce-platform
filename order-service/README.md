
# Order Service

## Overview

Order Service is responsible for creating and managing customer orders
in the E-Commerce Microservices platform.

It communicates synchronously with Inventory Service through REST APIs
to reserve and release product stock.

## Technology Stack

- Java 17
- Spring Boot 3.5.7
- Spring Data JPA
- Spring Web / RestClient
- PostgreSQL 17
- Maven
- Docker
- Logstash Logging

## Port and Database

| Property | Value |
|---|---|
| Service Port | 8083 |
| Database | order_db |
| Database Port | 5432 |
| Inventory Service | http://localhost:8082 |

## Database Design

### orders

- id
- customer_id
- status
- total_amount
- created_at
- updated_at

### order_items

- id
- order_id
- product_id
- quantity
- unit_price
- line_total

## Order Status

- PENDING
- CONFIRMED
- FAILED
- CANCELLED
- COMPENSATION_REQUIRED

## APIs

| Method | Endpoint | Description |
|---|---|---|
| POST | /api/orders | Create an order |
| GET | /api/orders/{id} | Get order by ID |
| GET | /api/orders | Get all orders |
| GET | /api/orders/customer/{customerId} | Get customer orders |
| POST | /api/orders/{id}/cancel | Cancel an order |

## Create Order Request

POST /api/orders

```json
{
  "customerId": 1,
  "items": [
    {
      "productId": 1001,
      "quantity": 2,
      "unitPrice": 25000.00
    },
    {
      "productId": 1002,
      "quantity": 3,
      "unitPrice": 500.00
    }
  ]
}
```

## Order Processing Flow

1. Create an order with PENDING status.
2. Save the order and its items in order_db.
3. Call Inventory Service to reserve each item.
4. If all reservations succeed, change the order status to CONFIRMED.
5. If a reservation fails, release previously successful reservations.
6. If compensation succeeds, set status to FAILED.
7. If compensation fails, set status to COMPENSATION_REQUIRED.

## Inventory Integration

Base URL:

http://localhost:8082

Reserve stock:

POST /api/inventory/reservations

Release stock:

POST /api/inventory/reservations/release

Request body:

```json
{
  "orderId": 1,
  "productId": 1001,
  "quantity": 2
}
```

## Local Configuration

Database:

- URL: jdbc:postgresql://localhost:5432/order_db
- Username: ecommerce
- Password: ecommerce123

Inventory URL:

http://localhost:8082

## Run the Service

Start infrastructure:

```bash
docker compose -f infrastructure/docker-compose.yml up -d
```

Build:

```bash
mvn -pl order-service clean package
```

Run:

```bash
mvn -pl order-service spring-boot:run
```

Health check:

```bash
curl http://localhost:8083/actuator/health
```

Run tests:

```bash
mvn -pl order-service test
```

## Important Design Decisions

- Order Service owns order_db.
- No cross-service database foreign keys.
- BigDecimal is used for monetary values.
- Inventory operations are performed through REST.
- Local database transactions do not cover Inventory Service.
- Compensation is used to release previously reserved stock.
- COMPENSATION_REQUIRED identifies orders that need recovery.

## Current Limitations

- Customer existence is not yet validated through Customer Service.
- Product prices are temporarily accepted from the request.
- A timeout can leave the reservation outcome uncertain.
- Compensation recovery is not automated yet.
- No Kafka events or transactional outbox yet.
- No payment processing yet.

These limitations will be addressed in upcoming lessons.

## Next Steps

- Integrate Kafka.
- Publish order events.
- Introduce Saga orchestration and recovery.
- Add transactional outbox.
- Integrate Payment Service.
