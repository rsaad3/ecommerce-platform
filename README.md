
# E-Commerce Microservices Platform

## Project Overview

A learning project that demonstrates how to build a scalable
E-Commerce platform using Java, Spring Boot, PostgreSQL,
Docker, Kafka, and ELK Stack.

The system is designed using Microservices Architecture.
Each service owns its database and communicates with other
services through APIs and messaging.

## Technology Stack

| Technology | Version / Purpose |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.7 |
| Spring Cloud | 2025.0.0 |
| Maven | 3.9+ |
| PostgreSQL | 17 |
| Kafka | 3.9.1 |
| Elasticsearch | 8.17.0 |
| Logstash | 8.17.0 |
| Kibana | 8.17.0 |
| Docker Compose | V2 |

## Architecture

Current services:

1. Customer Service
2. Inventory Service
3. Order Service

Planned services:

4. Payment Service
5. Notification Service

Additional planned components:

- Spring Cloud Gateway
- Kafka Event-Driven Communication
- Saga Pattern
- Transactional Outbox
- Centralized Logging
- Distributed Tracing

## Project Structure

```text
ecommerce-platform/
│
├── customer-service/
│
├── inventory-service/
│
├── order-service/
│
├── payment-service/          # Planned
│
├── notification-service/     # Planned
│
├── infrastructure/
│   ├── docker-compose.yml
│   ├── postgres/
│   │   └── init-databases.sql
│   ├── kafka/
│   └── elk/
│
├── pom.xml
└── README.md
```

## Microservices

### 1. Customer Service

Port: 8081

Database: customer_db

Responsibilities:

- Create customers
- Retrieve customers
- Update customer information
- Delete customers
- Validate customer email uniqueness
- Support pagination

APIs:

| Method | Endpoint |
|---|---|
| POST | /api/customers |
| GET | /api/customers/{id} |
| GET | /api/customers |
| PUT | /api/customers/{id} |
| DELETE | /api/customers/{id} |

### 2. Inventory Service

Port: 8082

Database: inventory_db

Responsibilities:

- Manage product stock quantities
- Reserve stock for orders
- Confirm reservations
- Release reservations
- Prevent overselling
- Support idempotent reservation operations

Main APIs:

| Method | Endpoint |
|---|---|
| POST | /api/inventory |
| GET | /api/inventory/{productId} |
| GET | /api/inventory |
| POST | /api/inventory/reservations |
| POST | /api/inventory/reservations/release |
| POST | /api/inventory/reservations/confirm |

Stock reservation uses atomic database updates to prevent
concurrent requests from reserving more stock than is available.

### 3. Order Service

Port: 8083

Database: order_db

Responsibilities:

- Create orders with multiple items
- Calculate order totals
- Manage order lifecycle
- Reserve stock through Inventory Service
- Compensate successful reservations when another reservation fails
- Cancel confirmed orders

APIs:

| Method | Endpoint |
|---|---|
| POST | /api/orders |
| GET | /api/orders/{id} |
| GET | /api/orders |
| GET | /api/orders/customer/{customerId} |
| POST | /api/orders/{id}/cancel |

Order statuses:

- PENDING
- CONFIRMED
- FAILED
- CANCELLED
- COMPENSATION_REQUIRED

Order creation currently uses synchronous REST integration
with Inventory Service.

## Database Architecture

Each microservice has its own database.

| Service | Database |
|---|---|
| Customer Service | customer_db |
| Inventory Service | inventory_db |
| Order Service | order_db |
| Payment Service | payment_db (planned) |
| Notification Service | notification_db (planned) |

Services must not directly access another service's database.

## Infrastructure Ports

| Component | Port |
|---|---:|
| PostgreSQL | 5432 |
| Kafka External | 9092 |
| Kafka Internal | 29092 |
| Elasticsearch | 9200 |
| Logstash TCP | 5000 |
| Kibana | 5601 |

## Application Ports

| Service | Port |
|---|---:|
| Customer Service | 8081 |
| Inventory Service | 8082 |
| Order Service | 8083 |
| Payment Service | 8084 (planned) |
| Notification Service | 8085 (planned) |
| API Gateway | 8080 (planned) |

## Prerequisites

- JDK 17
- Maven 3.9+
- Docker Desktop
- Docker Compose V2
- Postman or curl

## Database Initialization

The PostgreSQL initialization script should create:

```sql
CREATE DATABASE customer_db;
CREATE DATABASE inventory_db;
CREATE DATABASE order_db;
CREATE DATABASE payment_db;
CREATE DATABASE notification_db;
```

Note: PostgreSQL initialization scripts execute when the
database container initializes its data directory for the
first time. Existing Docker volumes may require manual
database creation.

## Start Infrastructure

```bash
docker compose -f infrastructure/docker-compose.yml up -d
```

Check running containers:

```bash
docker ps
```

## Build the Project

Build all modules:

```bash
mvn clean package
```

Build an individual service:

```bash
mvn -pl customer-service clean package
mvn -pl inventory-service clean package
mvn -pl order-service clean package
```

## Run Services

Run Customer Service:

```bash
mvn -pl customer-service spring-boot:run
```

Run Inventory Service:

```bash
mvn -pl inventory-service spring-boot:run
```

Run Order Service:

```bash
mvn -pl order-service spring-boot:run
```

Run each command in a separate terminal.

## Health Checks

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```

## Testing

Run all module tests:

```bash
mvn test
```

Run an individual module:

```bash
mvn -pl customer-service test
mvn -pl inventory-service test
mvn -pl order-service test
```

## Logging

Services send structured logs to Logstash.

Kibana:

http://localhost:5601

Example service filters:

```text
service : "customer-service"
```

```text
service : "inventory-service"
```

```text
service : "order-service"
```

## Current Order Processing Flow

1. Client sends an order request.
2. Order Service creates a PENDING order.
3. Order Service requests stock reservation from Inventory Service.
4. Inventory Service atomically reserves available quantities.
5. If every reservation succeeds, Order becomes CONFIRMED.
6. If any reservation fails, Order Service attempts compensation.
7. Failed compensation is recorded as COMPENSATION_REQUIRED.

## Architecture Decisions

- Database per service.
- No cross-service foreign keys.
- REST integration for the current Order-to-Inventory flow.
- Atomic SQL updates for inventory reservation.
- BigDecimal for monetary calculations.
- DTOs and validation at API boundaries.
- Separate persistence transactions from remote REST calls.
- Compensation for partial reservation failures.

## Learning Roadmap

| Lesson | Topic | Status |
|---|---|---|
| 1 | Project Setup and Architecture | Completed |
| 2 | Docker and PostgreSQL Infrastructure | Completed |
| 3 | Customer Service Design and Setup | Completed |
| 4 | Customer Service CRUD and Validation | Completed |
| 5 | Inventory Service and Stock Reservation | Completed |
| 6 | Order Service and Inventory Integration | Current |
| 7 | Kafka Integration | Planned |
| 8 | Event-Driven Order Processing | Planned |
| 9 | Payment Service | Planned |
| 10 | Notification Service | Planned |
| 11 | Saga Pattern | Planned |
| 12 | Transactional Outbox and Retry | Planned |
| 13 | API Gateway | Planned |
| 14 | Centralized Logging and Monitoring | Planned |
| 15 | Distributed Tracing and Resilience | Planned |
| 16 | Dockerize All Services | Planned |

## Next Lesson

Lesson 7: Kafka Integration

Topics:

- Kafka architecture
- Brokers, topics, partitions, and offsets
- Producers and consumers
- Consumer groups
- Spring Kafka configuration
- Docker Kafka connectivity
- Publishing and consuming test events
- Message serialization and error handling

