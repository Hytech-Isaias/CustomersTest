# OrionTek Customer-Locations 🇩🇴

RESTful web application for **OrionTek** to manage customers and their associated addresses ($N$ locations per customer).

Built with **Java 21**, **Spring Boot 3.4.1**, **PostgreSQL 16**, **Apache Kafka (KRaft mode)**, **Flyway**, **CQRS pattern**, **MapStruct**, **Spring Security (API Key)**, and **OpenAPI 3.0 / Swagger UI**.

---

## 🏛️ System Architecture & Architectural Directives

- **CQRS (Command Query Responsibility Segregation):**
  - **Command Flow:** Mutates state (`CreateCustomerCommand`, `AssignLocationToCustomerCommand`). Managed by `CustomerCommandHandler` (`@Transactional`), enforcing business invariants and publishing domain events to Kafka.
  - **Query Flow:** Read-only operations (`GetCustomerByIdQuery`, `ListCustomerAddressesQuery`). Managed by `CustomerQueryHandler` (`@Transactional(readOnly = true)`) returning optimized DTO projections.
- **Relational Schema (3NF/BCNF):**
  - `customers`: Master entity (`id`, `commercial_name`, `owner_name`, `email`, `phone`, `rnc`, `created_at`, `updated_at`, `deleted_at`).
  - `locations`: Reusable address master (`id`, `street_address`, `city`, `state_province`, `country`, `postal_code`).
  - `customer_locations`: M:N junction table (`customer_id`, `location_id`, `is_primary`, `assigned_at`) with partial unique constraint enforcing **at most 1 primary location per customer**.
- **Event-Driven Streaming (Apache Kafka):**
  - **Producer:** Publishes `CustomerCreatedEvent`, `CustomerUpdatedEvent`, and `LocationAssignedEvent` upon command execution.
  - **Seed Consumers:** Asynchronously ingests seed payloads from `oriontek.seed.customers` and `oriontek.seed.locations`.
- **API Key Security & RBAC:**
  - Stateless `X-API-Key` authentication.
  - `ROLE_ADMIN`: Write access (`POST`).
  - `ROLE_USER`: Read access (`GET`).
- **Resilience & Standards:**
  - Centralized RFC 7807 `ProblemDetail` exception responses via `@RestControllerAdvice`.
  - Correlation ID tracing via `MDC` and `X-Correlation-Id` headers.

---

## 🚀 Quick Start with Docker Compose (Recommended)

The entire infrastructure (PostgreSQL 16, KRaft Kafka single-node, and Spring Boot App) is containerized.

```bash
# Clone and navigate to project directory
cd CustomersTest

# Build and start all services
docker-compose up --build
```

### Verified Service URLs:

- **Swagger UI / OpenAPI Documentation:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Spec:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
- **Actuator Health Check:** [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)

---

## 🔑 Authentication & API Keys

All REST endpoints require the `X-API-Key` header:

| Role      | API Key Header Value | Access Level                |
| --------- | -------------------- | --------------------------- |
| **Admin** | `oriontek#2026`      | Full Access (`POST`, `GET`) |
| **User**  | `oriontek#2026`      | Read-Only (`GET`)           |

---

## 📡 REST API Endpoints Reference

### 1. Create Customer

- **Endpoint:** `POST /api/v1/customers`
- **Header:** `X-API-Key: oriontek#2026`
- **Request Payload:**

```json
{
  "commercialName": "Cervecería Nacional Dominicana S.A.",
  "ownerName": "Franklin León Jimenes",
  "email": "contacto@cnd.com.do",
  "phone": "809-482-3000",
  "rnc": "101-00051-2"
}
```

- **Response:** `201 Created` (`Location: /api/v1/customers/{uuid}`)

### 2. Assign Location to Customer

- **Endpoint:** `POST /api/v1/customers/{id}/locations`
- **Header:** `X-API-Key: oriontek#2026`
- **Request Payload:**

```json
{
  "streetAddress": "Av. Winston Churchill esq. Gustavo Mejía Ricart",
  "city": "Santo Domingo",
  "stateProvince": "Distrito Nacional",
  "country": "Dominican Republic",
  "postalCode": "10109",
  "isPrimary": true
}
```

- **Response:** `201 Created`

### 3. Get Customer by ID

- **Endpoint:** `GET /api/v1/customers/{id}`
- **Header:** `X-API-Key: oriontek#2026`
- **Response:** `200 OK` (Includes master customer details and array of linked locations).

### 4. List Customers (Paginated)

- **Endpoint:** `GET /api/v1/customers?page=0&size=10&sortBy=createdAt&direction=DESC`
- **Header:** `X-API-Key: oriontek#2026`
- **Response:** `200 OK` (Spring Data Page payload).

### 5. List Customer Locations

- **Endpoint:** `GET /api/v1/customers/{id}/locations`
- **Header:** `X-API-Key: oriontek#2026`
- **Response:** `200 OK` (List of location responses).

---

## 💻 Local Development Setup (Manual)

If running without Docker:

### Prerequisites

- JDK 21+
- PostgreSQL server listening on `localhost:5432` (`oriontek_db` database)
- Apache Kafka listening on `localhost:9092`

### Run Gradle Build & Start App

```bash
# Compile and test
./gradlew build

# Run application
./gradlew bootRun
```

---

## 🗄️ Database Migrations & Pre-loaded Seed Data

Flyway automatically applies SQL migrations on startup:

1. **`V1__init_schema.sql`**: Schema definition (`customers`, `locations`, `customer_locations`).
2. **`V2__seed_data.sql`**: Pre-loaded into the database with primary & branch address links across 12 provinces.

---

## 🧪 Running Unit & Integration Tests

```bash
./gradlew test
```
