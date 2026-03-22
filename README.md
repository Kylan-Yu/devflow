# DevFlow

<div align="center">

**[English](#english) | [中文](#中文)**

---

**📚 Other Versions:** [🇺🇸 English Only](README_EN.md) | [🇨🇳 中文 Only](README_CN.md)

---

</div>

---

## English

**DevFlow is a bilingual full-stack developer community project built for portfolio presentation and remote engineering interviews.**

### Project Overview

This project demonstrates production-ready engineering capabilities designed for 300k+ daily active users (DAU).

**Key Architecture Decisions for 300k+ DAU:**

- **Modular Monolith**: Maintains delivery speed while preserving clear module boundaries  
- **Explicit Redis Caching**: High-frequency read paths use predictable cache keys with short TTL  
- **Async Event Pipeline**: Interaction side effects decoupled through RabbitMQ + WebSocket delivery  
- **Cursor Pagination**: Performance-optimized pagination avoiding deep offset issues

### Highlights

- **User-facing community app** with register, login, feed, search, post detail, interactions, notifications, profile settings, and media upload
- **Admin moderation dashboard** with user status control, post visibility management, report review, and admin audit logs  
- **Token-based authentication** with refresh flow and JWT-protected WebSocket notifications
- **Redis cache coverage** for hot feed, post detail, unread count, and profile hot paths  
- **RabbitMQ-based async notification pipeline** with near-real-time WebSocket delivery
- **MinIO-backed avatar and post cover uploads**  
- **CI workflow plus integration tests** for key portfolio-ready flows

### Monorepo Structure

```text
apps/api    Spring Boot modular monolith backend
apps/web    React + TypeScript user-facing web app
apps/admin  React + TypeScript admin app
deploy      Docker Compose stack for local infrastructure
docs        Architecture, deployment, API, and interview notes
```

### Tech Stack

- **Backend**: Spring Boot 3, Java 17, Spring Security, Spring Data JPA, Flyway
- **Frontend**: React, TypeScript, Vite
- **Infrastructure**: MySQL, Redis, RabbitMQ, MinIO
- **Realtime**: WebSocket
- **API Docs**: OpenAPI / Swagger UI
- **Tooling**: Docker Compose, GitHub Actions CI

### Core Features

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

### Architecture Notes

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

### Local Quick Start

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

### Local URLs

- **API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Web**: `http://localhost:5173`
- **Admin**: `http://localhost:5174`
- **RabbitMQ Console**: `http://localhost:15672`
- **MinIO API**: `http://localhost:9000`
- **MinIO Console**: `http://localhost:9001`

### Demo Accounts

- **Admin bootstrap account**: `admin / Admin@123456`
- **Seed users**:
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- **Seed password**: `password`

### Portfolio Positioning

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

---

## 中文

**DevFlow 是一个面向作品集展示和远程工程岗位面试的中英双语全栈开发者社区项目。**

### 项目概述

本项目展示了为300k+日活跃用户(DAU)设计的生产级工程能力。

**300k+ DAU关键架构决策:**

- **模块化单体**: 在保持交付效率的同时保留清晰的模块边界
- **显式Redis缓存**: 高频读取接口使用显式缓存键名和短TTL
- **异步事件管道**: 互动副作用通过RabbitMQ解耦，再由WebSocket推送
- **游标分页**: 性能优化的分页，避免深度偏移问题

### 项目亮点

- **用户端社区应用**: 注册、登录、信息流、搜索、帖子详情、互动、通知、个人设置、媒体上传
- **管理员审核仪表板**: 用户状态控制、帖子可见性管理、举报审核、管理员审计日志
- **基于令牌的认证**: 刷新令牌流程和JWT保护的WebSocket通知
- **Redis缓存覆盖**: 热门信息流、帖子详情、未读数、个人资料热点路径
- **基于RabbitMQ的异步通知管道**: 近实时WebSocket推送
- **MinIO支持的头像和帖子封面上传**
- **CI工作流和集成测试**: 关键作品集就绪流程

### 仓库结构

```text
apps/api    Spring Boot 模块化单体后端
apps/web    React + TypeScript 用户端Web应用
apps/admin  React + TypeScript 管理端应用
deploy      Docker Compose 本地基础设施栈
docs        架构、部署、API和面试文档
```

### 技术栈

- **后端**: Spring Boot 3, Java 17, Spring Security, Spring Data JPA, Flyway
- **前端**: React, TypeScript, Vite
- **基础设施**: MySQL, Redis, RabbitMQ, MinIO
- **实时**: WebSocket
- **API文档**: OpenAPI / Swagger UI
- **工具**: Docker Compose, GitHub Actions CI

### 核心功能

- **认证**: 注册、登录、刷新令牌、登出
- **用户资料**: 显示名称、简介、语言偏好、头像上传
- **社区信息流**: 最新信息流、热门信息流、分类过滤、游标分页
- **搜索**: 关键词和分类帖子搜索，支持可分享查询URL
- **内容工作流**: 创建、编辑、删除、详情查看、封面上传
- **社交互动**: 点赞、收藏、评论、关注/取消关注
- **通知**: 未读数、列表、标记已读、WebSocket推送
- **举报和审核**: 举报帖子/用户、审核举报、隐藏帖子、禁用用户
- **管理员审计轨迹**: 追踪审核操作，包含操作员、目标和时间戳
- **双语体验**: web端、管理端和文档均支持中英文

### 架构说明

**后端架构:**

- **后端采用模块化单体结构**，在保持交付效率的同时保留清晰的模块边界。
- **高频读取接口使用显式Redis缓存**，并配合简单可解释的失效策略。
- **互动副作用通过RabbitMQ解耦**，再由WebSocket推送到前端。
- **整个项目按300k DAU的目标思路设计**，强调务实、可解释、适合面试展开的工程取舍。

**性能优化:**

- **游标分页**: 避免深度分页中的N+1查询问题
- **多层缓存**: Redis + 应用级缓存用于热点路径
- **数据库索引**: 为高频查询模式优化的索引
- **异步处理**: 非阻塞通知管道

**可扩展性设计:**

- **水平扩展**: Redis集群 + MySQL读副本就绪
- **模块边界**: 每个模块可独立部署
- **事件驱动架构**: 通过消息队列松耦合

### 本地启动

1. **在仓库根目录安装前端依赖**。
   ```bash
   npm ci
   ```

2. **启动本地基础设施服务**。
   ```bash
   cd deploy
   docker compose up -d mysql redis rabbitmq minio
   ```

3. **启动后端服务**。
   ```bash
   mvn -f apps/api/pom.xml spring-boot:run
   ```

4. **启动用户端**。
   ```bash
   npm run dev:web
   ```

5. **启动管理端**。
   ```bash
   npm run dev:admin
   ```

### 本地地址

- **API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Web**: `http://localhost:5173`
- **Admin**: `http://localhost:5174`
- **RabbitMQ Console**: `http://localhost:15672`
- **MinIO API**: `http://localhost:9000`
- **MinIO Console**: `http://localhost:9001`

### 演示账号

- **管理员引导账号**: `admin / Admin@123456`
- **种子用户**:
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- **种子密码**: `password`

### 作品集定位

**这个项目刻意保持"面试可讲清楚"的工程形态：**

- **清晰的模块边界**: 便于讨论关注点分离
- **真实的产品工作流**: 从注册到内容审核的完整用户旅程
- **务实的基础设施选择**: 单体vs微服务、缓存策略、异步处理
- **足够的讨论深度**: 缓存、异步管道、安全、治理和交付质量

**关键面试话题:**

- **为什么选择模块化单体而非微服务？**
- **如何处理缓存失效？**
- **解释你的游标分页实现。**
- **通知管道是如何工作的？**
- **你的性能优化策略是什么？**
- **如何确保数据一致性？**

---

<div align="center">

[⬆️ Back to Top](#devflow)

</div>
