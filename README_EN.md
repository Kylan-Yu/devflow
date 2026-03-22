# DevFlow

**DevFlow is a bilingual full-stack developer community project built for portfolio presentation and remote engineering interviews.**

## Project Overview

This project demonstrates production-ready engineering capabilities designed for 300k+ daily active users (DAU).

**Key Architecture Decisions for 300k+ DAU:**

- **Modular Monolith**: Maintains delivery speed while preserving clear module boundaries  
- **Explicit Redis Caching**: High-frequency read paths use predictable cache keys with short TTL  
- **Async Event Pipeline**: Interaction side effects decoupled through RabbitMQ + WebSocket delivery  
- **Cursor Pagination**: Performance-optimized pagination avoiding deep offset issues

## Highlights

- **User-facing community app** with register, login, feed, search, post detail, interactions, notifications, profile settings, and media upload
- **Admin moderation dashboard** with user status control, post visibility management, report review, and admin audit logs  
- **Token-based authentication** with refresh flow and JWT-protected WebSocket notifications
- **Redis cache coverage** for hot feed, post detail, unread count, and profile hot paths  
- **RabbitMQ-based async notification pipeline** with near-real-time WebSocket delivery
- **MinIO-backed avatar and post cover uploads**  
- **CI workflow plus integration tests** for key portfolio-ready flows

## Monorepo Structure

```text
apps/api    Spring Boot modular monolith backend
apps/web    React + TypeScript user-facing web app
apps/admin  React + TypeScript admin app
deploy      Docker Compose stack for local infrastructure
docs        Architecture, deployment, API, and interview notes
```

## Tech Stack

- **Backend**: Spring Boot 3, Java 17, Spring Security, Spring Data JPA, Flyway
- **Frontend**: React, TypeScript, Vite
- **Infrastructure**: MySQL, Redis, RabbitMQ, MinIO
- **Realtime**: WebSocket
- **API Docs**: OpenAPI / Swagger UI
- **Tooling**: Docker Compose, GitHub Actions CI

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

- The backend uses a **modular monolith structure** to keep delivery fast while preserving clear module boundaries.
- **Read-heavy endpoints use explicit Redis caching** with straightforward invalidation rules.
- **Interaction side effects are decoupled through RabbitMQ** and pushed to clients via WebSocket.
- **The repository is designed around a 300k DAU target mindset**, focusing on practical and explainable engineering tradeoffs.

**Performance Optimizations:**

- **Cursor Pagination**: Avoids N+1 query problems in deep pagination  
- **Multi-layer Caching**: Redis + application-level caching for hot paths  
- **Database Indexing**: Optimized indexes for high-frequency query patterns  
- **Async Processing**: Non-blocking notification pipeline  

**Scalability Design:**

- **Horizontal Scaling**: Redis cluster + MySQL read replicas ready  
- **Modular Boundaries**: Each module can be deployed independently  
- **Event-driven Architecture**: Loose coupling via message queues

## Local Quick Start

1. **Install frontend dependencies** at repository root.
   ```bash
   npm ci
   ```

2. **Start infrastructure services**.
   ```bash
   cd deploy
   docker compose up -d mysql redis rabbitmq minio
   ```

3. **Start the backend API**.
   ```bash
   mvn -f apps/api/pom.xml spring-boot:run
   ```

4. **Start the web app**.
   ```bash
   npm run dev:web
   ```

5. **Start the admin app**.
   ```bash
   npm run dev:admin
   ```

## Local URLs

- **API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Web**: `http://localhost:5173`
- **Admin**: `http://localhost:5174`
- **RabbitMQ Console**: `http://localhost:15672`
- **MinIO API**: `http://localhost:9000`
- **MinIO Console**: `http://localhost:9001`

## Demo Accounts

- **Admin bootstrap account**: `admin / Admin@123456`
- **Seed users**:
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- **Seed password**: `password`

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
