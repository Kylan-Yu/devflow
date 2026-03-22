# DevFlow | 开发者社区

[English](./README_EN.md) | [简体中文](./README_CN.md)

This project is a bilingual full-stack developer community built for portfolio presentation and remote engineering interviews.
该项目是一个面向作品集展示和远程工程岗位面试的中英双语全栈开发者社区。

## Quick Links | 快速入口
- Architecture | 架构说明: `docs/ARCHITECTURE.md`
- API Overview | 接口概览: `docs/API_OVERVIEW.md`
- Interview Highlights | 面试重点: `docs/INTERVIEW_HIGHLIGHTS.md`
- Local Setup | 本地启动: See sections below

## Quick Start | 快速启动
Prerequisites | 前置环境: `Java 17`, `Maven 3.9+`, `Node 20+`, `MySQL 8`, `Redis 7`, `RabbitMQ 3.13`, `MinIO` or Docker Desktop.

```bash
# Install frontend dependencies
npm ci

# Start infrastructure services
cd deploy
docker compose up -d mysql redis rabbitmq minio

# Start backend API
mvn -f apps/api/pom.xml spring-boot:run

# Start web app
npm run dev:web

# Start admin app
npm run dev:admin
```

Default accounts | 默认账号:
- Admin: `admin / Admin@123456`
- Users: `alice@devflow.local`, `bob@devflow.local`, `carol@devflow.local`, `david@devflow.local` (password: `password`)

## Local URLs | 本地地址
- **API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Web**: `http://localhost:5173`
- **Admin**: `http://localhost:5174`
- **RabbitMQ Console**: `http://localhost:15672`
- **MinIO Console**: `http://localhost:9001`

## Project Status | 当前状态
- Full bilingual support across web, admin, and documentation.
- Production-ready architecture designed for 300k+ DAU.
- High-concurrency optimizations for 10,000 concurrent requests.
- Complete user journey from registration to content moderation.
- CI workflow with integration tests and quality gates.
- Modular monolith structure with clear boundaries.
- Real-time notifications via WebSocket.
- Redis caching with intelligent invalidation.
- RabbitMQ async event pipeline.

## Tech Stack | 技术栈
- **Backend**: Spring Boot 3, Java 17, Spring Security, Spring Data JPA, Flyway
- **Frontend**: React, TypeScript, Vite
- **Infrastructure**: MySQL, Redis, RabbitMQ, MinIO
- **Realtime**: WebSocket
- **Engineering**: GitHub Actions CI, integration tests, bilingual documentation

For full details, please open `README_EN.md` or `README_CN.md`.
详细说明请查看 `README_EN.md` 或 `README_CN.md`。
