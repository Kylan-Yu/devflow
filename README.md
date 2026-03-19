# DevFlow | Full-Stack Portfolio Project for Engineering Interviews

## 1. Project Overview
DevFlow is a bilingual (English/Chinese) full-stack To-C developer community project built with **React + Spring Boot**.

This repository is designed as a **portfolio-ready public project** for engineering interviews, with clear architecture decisions, production-style module boundaries, and features that are easy to explain during technical discussions.

## 2. Core Features
- User authentication and profile management (register, login, token refresh, logout, language preference)
- Post system (create, edit, delete, detail view, tags, categories)
- Feed system (latest feed, hot feed, cursor-based pagination)
- Interaction and notification features (like, favorite, comment, follow)
- Asynchronous notification pipeline using RabbitMQ
- Real-time notification delivery with WebSocket
- Bilingual UI for both web and admin applications

## 3. Why This Project Stands Out
- Full-stack implementation with clear module ownership
- Realistic To-C community domain with read-heavy traffic patterns
- Architecture designed with a **300k DAU peak scenario** in mind
- Redis caching strategy for hot read paths
- RabbitMQ-based asynchronous events plus WebSocket real-time push
- Docker Compose local environment for quick evaluation and demo

## 4. Tech Stack
- **Backend:** Spring Boot 3, Java 21, Spring Security, Spring Data JPA, Flyway
- **Frontend:** React, TypeScript, Vite (`apps/web`, `apps/admin`)
- **Data & Infrastructure:** MySQL, Redis, RabbitMQ, MinIO
- **Realtime:** WebSocket
- **API Docs:** OpenAPI / Swagger
- **Deployment:** Docker Compose

## 5. Architecture at a Glance
### Monorepo structure
- `apps/api`: modular monolith backend
- `apps/web`: user-facing application
- `apps/admin`: admin application
- `deploy`: local infrastructure orchestration

### Key architecture goals
- Keep delivery speed high with a modular monolith approach
- Optimize high-frequency read paths with a simple and clear cache strategy
- Decouple side effects through asynchronous event-driven flow

### Related documents
- [Architecture Diagram](./docs/ARCHITECTURE_DIAGRAM.md)
- [API Overview](./docs/API_OVERVIEW.md)

## 6. Run Locally
### Quick start
```bash
cd deploy
docker compose up -d