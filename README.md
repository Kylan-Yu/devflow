# DevFlow

<<<<<<< HEAD
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
=======
Spring Boot + React monorepo for a forum-style developer community project (portfolio use).

## 1. Project Overview
DevFlow is a personal full-stack project focused on high-interaction community workflows: posting, commenting, liking, following, notifications, reporting, and admin moderation.

It contains:
- `apps/api`: Spring Boot API (modular monolith)
- `apps/web`: user-facing React app
- `apps/admin`: admin moderation React app
- `deploy`: local Docker Compose stack

## 2. Why I Built This Project
I built this project to demonstrate backend and full-stack engineering skills for user-facing discussion systems:
- modeling core social entities and moderation flows
- implementing secure API + session lifecycle
- handling read-heavy paths with caching and cursor pagination
- showing async notification processing and local deployability

## 3. My Role
I am the sole builder of this repository: architecture, backend modules, frontend pages, admin app, SQL migrations, Docker setup, and CI workflow.

## 4. Core User Scenarios
- Register/login, refresh token, logout
- View latest/hot feeds and search posts
- Create/edit/delete posts, upload cover image
- Like/favorite/comment on posts
- Follow/unfollow users
- Receive notifications (API + WebSocket push)
- Report posts/users and track personal report status
- Review reports and moderate content in admin dashboard

## 5. Current Implementation Status
### Implemented
- Backend modules for auth, user profile, posts/catalog, interactions, feed, notifications, reports, admin moderation, media upload
- Redis-backed caching on selected read paths (feed first page, post detail, unread count, profile)
- RabbitMQ event flow for interaction-triggered notifications
- WebSocket endpoint `/ws/notifications` with JWT-based handshake validation
- Flyway migrations (`V1` to `V8`) and Swagger/OpenAPI endpoints
- Web app routes for home/feed/search/post detail/editor/profile/settings/notifications/reports
- Admin app routes for login and dashboard with user/post/report/audit operations

### Partially Implemented
- Chinese localization quality is inconsistent in multiple files (visible mojibake/encoding issues)
- Admin report list API currently returns a filtered list (status + size), but not the same pagination/search model used by other admin tables
- Counter optimization layer is in transition:
  - `post_counters` table and repository updates are used
  - some counter/cache code paths are scaffolded or temporarily disabled
  - debug logging remains in interaction/counter services
- High-concurrency profile/settings exist (`application-high-concurrency.yml`) but there is no benchmark evidence in this repo
- `deploy/mysql/devflow_full_init.sql` does not include newer tables (reports/admin audit/counter tables), so Flyway is the reliable schema source

### Planned / Roadmap
- Clean up i18n encoding and unify bilingual copy quality
- Unify admin report API to full pagination/search contract
- Harden counter and cache paths (remove debug output, tighten error handling)
- Add frontend automated tests and expand CI checks

## 6. System Architecture
Monorepo + modular monolith backend:

```text
apps/api
  common
  modules/auth
  modules/user
  modules/post
  modules/feed
  modules/interaction
  modules/notification
  modules/media
  modules/report
  modules/admin
  modules/search
apps/web
apps/admin
deploy
docs
```

Runtime components:
- MySQL for transactional data
- Redis for cache
- RabbitMQ for async notification events
- MinIO for avatar/cover uploads
- WebSocket channel for notification pushes

## 7. Implemented Modules
### Backend (`apps/api`)
- `auth`: user/admin login flows, JWT issuing/parsing, refresh token persistence
- `user`: profile read/update, language preference, avatar URL
- `post`: CRUD, catalog (categories/tags), author post list
- `feed`: latest/hot cursor-based feed endpoints
- `interaction`: likes, favorites, comments, follows
- `notification`: unread/list/read APIs + event handling + WebSocket push
- `report`: post/user report submission and review outcomes
- `admin`: overview metrics, user/post moderation, audit log listing
- `media`: MinIO-backed avatar and post-cover uploads
- `search`: keyword/category search over published posts

### Frontend (`apps/web`)
- Auth pages: login/register
- Content pages: latest/hot feed, search, post detail, post editor
- User pages: profile, settings, user posts
- Notification page and report history page

### Admin (`apps/admin`)
- Admin login page
- Dashboard tabs for user moderation, post moderation, report review, audit logs

## 8. Tech Stack
- Backend: Spring Boot 3.3, Java 17 target, Spring Security, Spring Data JPA, Flyway, springdoc-openapi
- Frontend: React 18, TypeScript, Vite, react-router, i18next
- Infra: MySQL 8, Redis 7, RabbitMQ 3, MinIO
- CI: GitHub Actions (`.github/workflows/ci.yml`) running web/admin builds and backend tests

## 9. Local Setup
### Prerequisites
- Java 17
- Maven 3.9+
- Node.js 20+
- Docker (recommended for infra services)

### Start services
```bash
# 1) install frontend dependencies
npm ci

# 2) start infra
cd deploy
docker compose up -d mysql redis rabbitmq minio

# 3) run backend
>>>>>>> 0e5046120c37e226ec5cd7cb66cff593cda37866
mvn -f apps/api/pom.xml spring-boot:run
```

<<<<<<< HEAD
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
=======
# 4) run web
npm run dev:web

# 5) run admin
npm run dev:admin
```

### URLs
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Web: `http://localhost:5173`
- Admin: `http://localhost:5174`
- RabbitMQ console: `http://localhost:15672`
- MinIO console: `http://localhost:9001`

### Accounts
- Admin bootstrap account (from `application.yml`): `admin / Admin@123456`
- Flyway `V4` migration inserts demo users (`alice/bob/carol/david` emails)

## 10. Key Engineering Decisions
- Modular monolith boundaries instead of early microservice split
- Unified API response shape (`code`, `message`, `data`, `traceId`) and frontend message-code mapping
- Cursor pagination for feed/search/user-post endpoints
- Explicit cache keys + TTL + eviction on write paths
- Publish notification events after transaction commit, then consume asynchronously

## 11. Current Limitations
- Localization quality is uneven (especially Chinese text encoding in several resources/comments)
- Some optimization/scalability code is present but not fully integrated into the active request path
- Frontend test suites are not present yet
- Docker Compose currently targets infra + API; web/admin are expected to run with Vite in local dev
- Default secrets/credentials are in local config and must be replaced before any public deployment

## 12. Roadmap
- Fix encoding issues and complete bilingual copy cleanup
- Standardize admin report pagination/search behavior
- Refine counter consistency and cache strategy implementation
- Add frontend tests and stricter CI quality gates
- Improve deployment packaging for one-command full-stack startup
>>>>>>> 0e5046120c37e226ec5cd7cb66cff593cda37866
