# 📅 Reservation Service

**Reservation Service** is a microservice of the **Food Delivery System**, responsible for managing table bookings and slot availability.

It handles event-driven updates through **Apache Kafka**.

---

## 🛠️ Tech Stack

- **Java 24**
- **Spring Boot 3.4.4**
- **PostgreSQL 17** (Database)
- **Flyway** (Database Migrations)
- **OpenFeign** (REST Client)
- **Apache Kafka** (Event-driven reservations)
- **SpringDoc OpenAPI** (Swagger Documentation)
- **Docker & Docker Compose**

---

## 🚀 Getting Started

### 1. Prerequisites

- Docker & Docker Desktop installed.
- Ensure the shared network `store-network` exists:

```bash
docker network create store-network
```

### 2. Run with Docker Compose

The system is fully containerized. To start the reservation ecosystem:

```bash
docker compose up -d --build
```

### 📦 Services & Access

| Service | URL / Access | Description |
|---------|--------------|------------|
| API Endpoints | http://localhost:8082 | Main REST API |
| Swagger UI | http://localhost:8082/swagger-ui.html | Interactive API Documentation |
| PgAdmin | http://localhost:7778 | Database management UI |
| PostgreSQL | reservation_db:5432 | Internal database address |

### ⚙️ Configuration

Environment variables managed via `.env` file:

| Key | Description |
|-----|------------|
| `PORT` | Service port (8082) |
| `POSTGRES_DB` | Database name (`reservation_db`) |
| `KAFKA_SERVER` | Kafka connection (`kafka:9092`) |

### Database Migrations

- Managed by Flyway.
- SQL scripts located at `./migrations`.
- Automatically applied to `reservation_db` on startup.

---

👤 **Author:** Nelson Almeida  
🌐 **Website:** [nelsonalmeida.pt](https://nelsonalmeida.pt)  
🐙 **GitHub:** [nelsonalmeida2](https://github.com/nelsonalmeida2)

> This project is for educational and portfolio purposes.

