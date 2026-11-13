
# E-Commerce Microservices Platform

Version: 2.0

A microservices-based E-Commerce application built using
Java 17, Spring Boot, PostgreSQL, Docker Compose, Kafka,
Spring Cloud Gateway, and ELK Stack.

---

## 1. Project Overview

The platform is designed using independent microservices.

Each service owns its business logic and its own database.

### Planned Microservices

- Customer Service
- Inventory Service
- Order Service
- Payment Service
- Notification Service
- API Gateway

### Architecture Principles

- Microservices Architecture
- Database per Service
- REST APIs for synchronous communication
- Kafka for asynchronous communication
- Centralized Logging
- Independent Service Deployment
- DTO-based API contracts
- Transaction Management
- Idempotent Operations
- Concurrency Control

---

## 2. Technology Stack

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
| Spring Data JPA | Persistence |
| Hibernate | ORM |
| Jakarta Validation | Request validation |
| JUnit 5 | Unit testing |
| Mockito | Mocking |

---

## 3. Project Structure

```text
ecommerce-platform/
│
├── pom.xml
├── README.md
│
├── infrastructure/
│   ├── docker-compose.yml
│   │
│   ├── postgres/
│   │   └── init-databases.sql
│   │
│   ├── logstash/
│   │   └── pipeline/
│   │       └── logstash.conf
│   │
│   └── kibana/
│       └── kibana.yml
│
├── customer-service/
│   ├── pom.xml
│   └── src/
│
├── inventory-service/
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/ecommerce/inventory/
│           │   ├── controller/
│           │   ├── dto/
│           │   ├── entity/
│           │   ├── exception/
│           │   ├── filter/
│           │   ├── repository/
│           │   ├── service/
│           │   └── InventoryServiceApplication.java
│           │
│           └── resources/
│               ├── application.yml
│               └── logback-spring.xml
│
├── order-service/
│
├── payment-service/
│
├── notification-service/
│
└── api-gateway/
```

---

## 4. Database Architecture

Each microservice owns its database.

| Service | Database |
|---|---|
| Customer Service | customer_db |
| Inventory Service | inventory_db |
| Order Service | order_db |
| Payment Service | payment_db |
| Notification Service | notification_db |

Services must not directly access another service's database.

Communication should happen through APIs or events.

---

## 5. Infrastructure Ports

| Component | Port |
|---|---:|
| API Gateway | 8080 |
| Customer Service | 8081 |
| Order Service | 8082 |
| Payment Service | 8083 |
| Notification Service | 8084 |
| Inventory Service | 8085 |
| PostgreSQL | 5432 |
| Kafka External | 9092 |
| Kafka Internal | 29092 |
| Elasticsearch | 9200 |
| Logstash TCP | 5000 |
| Kibana | 5601 |

---

## 6. Running Infrastructure

Start infrastructure:

```bash
docker compose \
-f infrastructure/docker-compose.yml \
up -d
```

Check containers:

```bash
docker compose \
-f infrastructure/docker-compose.yml \
ps
```

View logs:

```bash
docker compose \
-f infrastructure/docker-compose.yml \
logs -f
```

---

## 7. Customer Service

Customer Service manages customer information.

Implemented features:

- Create Customer
- Get Customer by ID
- Get Customers with Pagination
- Update Customer
- Delete Customer
- DTO Mapping
- Bean Validation
- Global Exception Handling
- Duplicate Email Protection
- PostgreSQL Persistence
- Transaction Management
- Correlation ID Logging

### Customer Endpoints

| Method | Endpoint |
|---|---|
| POST | /api/customers |
| GET | /api/customers/{id} |
| GET | /api/customers?page=0&size=10 |
| PUT | /api/customers/{id} |
| DELETE | /api/customers/{id} |

### Run Customer Service

```bash
mvn -pl customer-service clean package
```

```bash
mvn -pl customer-service spring-boot:run
```

Health Check:

```text
http://localhost:8081/actuator/health
```

---

## 8. Inventory Service

Inventory Service manages product stock and reservations.

Implemented features:

- Create Inventory Item
- Get Inventory
- Add Stock
- Reserve Stock
- Confirm Reservation
- Release Reservation
- Reservation Status Management
- Idempotent Reservation Requests
- Atomic PostgreSQL Updates
- Optimistic Locking with @Version
- Prevent Negative Inventory
- Transaction Management
- Correlation ID Logging
- Unit Tests with Mockito

### Inventory Data Model

Inventory Item:

- productId
- sku
- availableQuantity
- reservedQuantity
- version
- createdAt
- updatedAt

Inventory Reservation:

- reservationId
- orderId
- productId
- quantity
- status
- createdAt
- updatedAt

### Reservation States

- RESERVED
- CONFIRMED
- RELEASED

### Inventory Endpoints

| Method | Endpoint |
|---|---|
| POST | /api/inventory/items |
| GET | /api/inventory/items/{productId} |
| POST | /api/inventory/items/{productId}/stock |
| PUT | /api/inventory/reservations/{reservationId} |
| GET | /api/inventory/reservations/{reservationId} |
| POST | /api/inventory/reservations/{reservationId}/confirm |
| POST | /api/inventory/reservations/{reservationId}/release |

### Run Inventory Service

```bash
mvn -pl inventory-service -am clean package
```

```bash
java -jar inventory-service/target/inventory-service-1.0.0.jar
```

Health Check:

```text
http://localhost:8085/actuator/health
```

---

## 9. Inventory Test Examples

Create Inventory:

```http
POST http://localhost:8085/api/inventory/items
```

```json
{
  "productId": 1001,
  "sku": "SKU-1001",
  "initialQuantity": 10
}
```

Reserve Stock:

```http
PUT http://localhost:8085/api/inventory/reservations/11111111-1111-1111-1111-111111111111
```

```json
{
  "orderId": "22222222-2222-2222-2222-222222222222",
  "productId": 1001,
  "quantity": 3
}
```

Confirm Reservation:

```http
POST http://localhost:8085/api/inventory/reservations/11111111-1111-1111-1111-111111111111/confirm
```

Release Reservation:

```http
POST http://localhost:8085/api/inventory/reservations/11111111-1111-1111-1111-111111111111/release
```

---

## 10. Running Tests

Run Customer Service tests:

```bash
mvn -pl customer-service test
```

Run Inventory Service tests:

```bash
mvn -pl inventory-service -am test
```

Build the implemented modules:

```bash
mvn -pl customer-service,inventory-service -am clean package
```

---

## 11. PostgreSQL Access

Connect to PostgreSQL:

```bash
docker exec -it ecommerce-postgres \
psql -U ecommerce -d inventory_db
```

List tables:

```sql
\dt
```

Check Inventory:

```sql
SELECT * FROM inventory_items;
```

Check Reservations:

```sql
SELECT * FROM inventory_reservations;
```

---

## 12. Kafka

Kafka is configured in the infrastructure layer.

External bootstrap server:

```text
localhost:9092
```

Internal bootstrap server:

```text
kafka:29092
```

Create a sample topic:

```bash
docker exec -it ecommerce-kafka \
/opt/kafka/bin/kafka-topics.sh \
--bootstrap-server localhost:9092 \
--create \
--topic order-created \
--partitions 3 \
--replication-factor 1
```

Kafka application event integration is planned for a later stage.

---

## 13. Centralized Logging

The logging pipeline:

```text
Microservices
     |
     v
 Logback
     |
     v
 Logstash
     |
     v
Elasticsearch
     |
     v
  Kibana
```

Kibana:

```text
http://localhost:5601
```

Elasticsearch:

```text
http://localhost:9200
```

Logstash TCP:

```text
localhost:5000
```

Recommended Data View:

```text
ecommerce-logs-*
```

Use the X-Correlation-Id header to trace requests.

---

## 14. Implemented Features

- [x] Project Maven structure
- [x] Docker infrastructure
- [x] PostgreSQL databases
- [x] Kafka infrastructure
- [x] Elasticsearch
- [x] Logstash
- [x] Kibana
- [x] Customer Service CRUD implementation
- [x] Customer validation and exception handling
- [x] Inventory Service implementation
- [x] Stock reservation
- [x] Reservation confirmation and release
- [x] Idempotency for reservations
- [x] Atomic stock updates
- [x] Optimistic locking
- [x] Inventory unit test examples

---

## 15. Development Roadmap

1. Complete Customer Service practical testing.
2. Complete Inventory Service practical testing.
3. Add PostgreSQL Testcontainers integration tests.
4. Add Flyway migrations.
5. Build Order Service.
6. Integrate Order Service with Inventory Service.
7. Build Payment Service.
8. Build Notification Service.
9. Integrate Kafka producers and consumers.
10. Implement Saga Pattern.
11. Implement Transactional Outbox.
12. Build Spring Cloud Gateway.
13. Add distributed tracing.
14. Add Resilience4j.
15. Add OAuth2 / Keycloak security.
16. Add end-to-end integration tests.

---

## 16. Current Development Status

Completed in the lessons:

- Infrastructure setup.
- Customer Service CRUD implementation.
- Inventory Service stock and reservation implementation.

Next step:

Build Order Service and integrate it with Customer and Inventory Services.

---

## 17. Environment and Security Notes

The database credentials in application.yml are for local development only.

For production:

- Use environment variables or a secrets manager.
- Use Flyway database migrations.
- Do not expose internal databases publicly.
- Secure Actuator endpoints.
- Configure TLS.
- Add authentication and authorization.
- Configure production logging and monitoring.
