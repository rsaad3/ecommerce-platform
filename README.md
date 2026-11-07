
# E-Commerce Microservices Platform

A practical E-Commerce Microservices project built
using Java 17, Spring Boot, Docker Compose, PostgreSQL,
Apache Kafka, and ELK Stack.

## 1. Architecture

The platform consists of the following services:

- Customer Service
- Order Service
- Payment Service
- Inventory Service
- Notification Service
- API Gateway (Spring Cloud Gateway)

Infrastructure:

- PostgreSQL
- Apache Kafka
- Elasticsearch
- Logstash
- Kibana
- Docker Compose

## 2. Technology Stack

| Technology | Version |
|---|---|
| Java | 17 |
| Spring Boot | 3.5.7 |
| Spring Cloud | 2025.0.0 |
| Maven | 3.9+ |
| PostgreSQL | 17 |
| Apache Kafka | 3.9.1 |
| Elasticsearch | 8.17.0 |
| Logstash | 8.17.0 |
| Kibana | 8.17.0 |
| Docker Compose | V2 |

## 3. Project Structure

```text
ecommerce-platform/
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
├── order-service/
├── payment-service/
├── inventory-service/
├── notification-service/
├── api-gateway/
│
├── pom.xml
└── README.md
```


## 4. Database Design

PostgreSQL databases:

- customer_db
- order_db
- payment_db
- inventory_db
- notification_db

Each microservice owns its database.

## 5. Infrastructure Ports

| Component | Port |
|---|---|
| PostgreSQL | 5432 |
| Kafka External | 9092 |
| Kafka Internal | 29092 |
| Elasticsearch | 9200 |
| Logstash TCP | 5000 |
| Kibana | 5601 |
| Customer Service | 8081 |

## 6. Start Infrastructure

Navigate to the project root.

Run:

docker compose -f infrastructure/docker-compose.yml up -d

Check containers:

docker compose -f infrastructure/docker-compose.yml ps

View logs:

docker compose -f infrastructure/docker-compose.yml logs -f

## 7. Run Customer Service

Build:

mvn -pl customer-service clean package

Run:

mvn -pl customer-service spring-boot:run

Customer Service URL:

http://localhost:8081

Health Check:

http://localhost:8081/actuator/health

## 8. Customer APIs

Base URL:

http://localhost:8081/api/customers

Create Customer:

POST /api/customers

Get Customer:

GET /api/customers/{id}

Get All Customers:

GET /api/customers?page=0&size=10

Update Customer:

PUT /api/customers/{id}

Delete Customer:

DELETE /api/customers/{id}

## 9. Logging Architecture

Spring Boot
|
v
SLF4J
|
v
Logback
|
v
Logstash TCP Appender
|
v
Logstash
|
v
Elasticsearch
|
v
Kibana

Kibana:

http://localhost:5601

Elasticsearch:

http://localhost:9200

Data View:

ecommerce-logs-*

Correlation Header:

X-Correlation-Id

## 10. Kafka

Kafka Bootstrap Servers:

localhost:9092

Docker Internal Address:

kafka:29092

Create Topic:

docker exec -it ecommerce-kafka \
/opt/kafka/bin/kafka-topics.sh \
--bootstrap-server localhost:9092 \
--create \
--topic order-created \
--partitions 3 \
--replication-factor 1

## 11. Testing

Check PostgreSQL:

docker exec -it ecommerce-postgres \
psql -U ecommerce -d customer_db

Check Kafka:

docker exec -it ecommerce-kafka \
/opt/kafka/bin/kafka-topics.sh \
--bootstrap-server localhost:9092 --list

Check Elasticsearch:

curl http://localhost:9200/_cluster/health?pretty

Check Customer Service:

curl http://localhost:8081/actuator/health

## 12. Implemented Features

- Initial Maven Multi-Module structure.
- Java 17 and Spring Boot setup.
- Docker Compose infrastructure.
- PostgreSQL database initialization.
- Kafka KRaft broker.
- Elasticsearch, Logstash, Kibana.
- Centralized JSON Logging configuration.
- Correlation ID using MDC.
- Customer Entity and DTOs.
- Customer Repository.
- Customer Business Service.
- Customer CRUD REST APIs.
- Pagination.
- Bean Validation.
- Global Exception Handling.

## 13. Development Roadmap

1. Complete Customer Service testing.
2. Add Flyway database migrations.
3. Implement Inventory Service.
4. Implement Order Service.
5. Implement Payment Service.
6. Implement Notification Service.
7. Integrate Apache Kafka.
8. Implement Spring Cloud Gateway.
9. Add Distributed Tracing.
10. Add Resilience4j.
11. Add OAuth2 and Keycloak.
12. Add Integration Tests.
13. Complete End-to-End testing.

## 14. Environment Notes

The current configuration is intended for local
development and learning.

Do not use the development credentials or disabled
Elasticsearch security settings in production.

Production deployments should use secure credentials,
TLS, database migrations, health checks, and
appropriate resource limits.
