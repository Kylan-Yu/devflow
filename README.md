# DevFlow

This repository is a **bilingual full-stack developer community** built for portfolio presentation and remote engineering interviews.

## Why this project
- Built to demonstrate production-ready engineering capabilities for 300k+ daily active users (DAU).
- Covers complete user journey: registration, content creation, social interactions, moderation, and notifications.
- Designed with modular monolith architecture that can evolve to microservices when needed.
- Optimized for high concurrency (10,000+ concurrent requests) with atomic counters and distributed locking.

## Tech Stack
- **Frontend**: React + TypeScript + Vite
- **Backend**: Spring Boot 3 + Java 17 + Spring Security + Spring Data JPA + Flyway
- **Infrastructure**: MySQL + Redis + RabbitMQ + MinIO
- **Realtime**: WebSocket with JWT protection
- **Engineering**: GitHub Actions CI, integration tests, bilingual documentation

## Prerequisites
- Java 17
- Maven 3.9+
- Node.js 20+ / npm 10+
- MySQL 8, Redis 7, RabbitMQ 3.13, MinIO; or Docker Desktop + Docker Compose

## Monorepo Structure
```text
apps/api    Spring Boot modular monolith backend
apps/web    React + TypeScript user-facing web app
apps/admin  React + TypeScript admin app
deploy      Docker Compose stack for local infrastructure
docs        Architecture, deployment, API, and interview notes
```

## Quick Start
### 1) Prepare environment
- Install frontend dependencies at repository root: `npm ci`
- Start infrastructure services: `cd deploy && docker compose up -d mysql redis rabbitmq minio`

### 2) Start backend API
```bash
mvn -f apps/api/pom.xml spring-boot:run
```

### 3) Start frontend apps
```bash
# User-facing web app
npm run dev:web

# Admin moderation dashboard
npm run dev:admin
```

### 4) Access services
- **API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Web**: `http://localhost:5173`
- **Admin**: `http://localhost:5174`
- **RabbitMQ Console**: `http://localhost:15672`
- **MinIO Console**: `http://localhost:9001`

### 5) Default accounts
- **Admin**: `admin / Admin@123456`
- **Seed users**:
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- **Password**: `password`

## Core Features
- **Authentication**: register, login, refresh token, logout
- **User profile**: display name, bio, language preference, avatar upload
- **Community feed**: latest feed, hot feed, category filter, cursor pagination
- **Search**: keyword and category-based post search with shareable query URLs
- **Content workflow**: create, edit, delete, detail view, cover upload
- **Social interactions**: like, favorite, comment, follow / unfollow
- **Notifications**: unread count, list, mark-as-read, WebSocket push
- **Reports and moderation**: report posts/users, review reports, hide posts, disable users
- **Admin audit trail**: trace moderation actions with operator, target, and timestamp
- **Bilingual experience**: `en-US` and `zh-CN` across web, admin, and docs

## Architecture Notes
**Backend Architecture:**
- **Modular Monolith**: Maintains delivery speed while preserving clear module boundaries
- **Explicit Redis Caching**: High-frequency read paths use predictable cache keys with short TTL
- **Async Event Pipeline**: Interaction side effects decoupled through RabbitMQ + WebSocket delivery
- **Cursor Pagination**: Performance-optimized pagination avoiding deep offset issues

**Performance Optimizations:**
- **Cursor Pagination**: Avoids N+1 query problems in deep pagination
- **Multi-layer Caching**: Redis + application-level caching for hot paths
- **Database Indexing**: Optimized indexes for high-frequency query patterns
- **Async Processing**: Non-blocking notification pipeline

**High Concurrency Design:**
- **Atomic Counters**: Separate counter tables to avoid hot row updates
- **Distributed Locking**: Redis-based locks for critical operations
- **Event Aggregation**: Batch processing to reduce message queue pressure
- **Connection Pooling**: Optimized pools for database, Redis, and RabbitMQ

## Testing
### Backend
```bash
cd apps/api
mvn test
```

### Frontend
```bash
# Web app
cd apps/web
npm run test
npm run build

# Admin app
cd apps/admin
npm run test
npm run build
```

## CI
- **GitHub Actions workflow**: `.github/workflows/ci.yml`
- **Runs**:
  - Backend tests (Java 17)
  - Frontend tests + build (Node 20)
  - Integration tests for key flows

## High Concurrency Mode
For testing 10,000 concurrent requests:
```bash
# Start with high-concurrency profile
java -jar -Dspring.profiles.active=high-concurrency apps/api/target/devflow-api.jar
```

**Key optimizations for 10k concurrent:**
- HikariCP: 100 database connections
- Redis: 50 active connections
- RabbitMQ: 20 consumers, 50 prefetch
- Tomcat: 500 max threads
- Atomic counters for hot updates
- Event aggregation for notifications

## Portfolio Positioning
**This project is intentionally built to be easy to explain in interviews:**
- **Clear module boundaries**: Easy to discuss separation of concerns
- **Realistic product workflows**: Complete user journey from registration to content moderation
- **Practical infrastructure choices**: Monolith vs microservices, caching strategies, async processing
- **Enough depth to discuss**: Caching, async pipelines, security, governance, and delivery quality

**Key Interview Topics:**
- **Why modular monolith over microservices?**
- **How do you handle cache invalidation?**
- **Explain your cursor pagination implementation.**
- **How does the notification pipeline work?**
- **What are your performance optimization strategies?**
- **How do you ensure data consistency?**
- **How does your system handle 10,000 concurrent requests?**

## Docs
- **Architecture**: `docs/ARCHITECTURE.md`
- **API Overview**: `docs/API_OVERVIEW.md`
- **Interview Highlights**: `docs/INTERVIEW_HIGHLIGHTS.md`

---

If this project is used in interviews, start with the architecture and core modules, then demo high-concurrency optimizations, cursor pagination, and real-time notifications.
