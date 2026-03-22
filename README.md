# DevFlow

**DevFlow is a bilingual full-stack developer community project built for portfolio presentation and remote engineering interviews.**  
**DevFlow 是一个面向作品集展示和远程工程岗位面试的中英双语全栈开发者社区项目。**

## Project Overview | 项目概述

This project demonstrates production-ready engineering capabilities designed for 300k+ daily active users (DAU).  
本项目展示了为300k+日活跃用户(DAU)设计的生产级工程能力。

**Key Architecture Decisions for 300k+ DAU:**  
**300k+ DAU关键架构决策:**

- **Modular Monolith**: Maintains delivery speed while preserving clear module boundaries  
  **模块化单体**: 在保持交付效率的同时保留清晰的模块边界
- **Explicit Redis Caching**: High-frequency read paths use predictable cache keys with short TTL  
  **显式Redis缓存**: 高频读取接口使用显式缓存键名和短TTL
- **Async Event Pipeline**: Interaction side effects decoupled through RabbitMQ + WebSocket delivery  
  **异步事件管道**: 互动副作用通过RabbitMQ解耦，再由WebSocket推送
- **Cursor Pagination**: Performance-optimized pagination avoiding deep offset issues  
  **游标分页**: 性能优化的分页，避免深度偏移问题

## Highlights | 项目亮点

- **User-facing community app** with register, login, feed, search, post detail, interactions, notifications, profile settings, and media upload
  **用户端社区应用**: 注册、登录、信息流、搜索、帖子详情、互动、通知、个人设置、媒体上传
- **Admin moderation dashboard** with user status control, post visibility management, report review, and admin audit logs  
  **管理员审核仪表板**: 用户状态控制、帖子可见性管理、举报审核、管理员审计日志
- **Token-based authentication** with refresh flow and JWT-protected WebSocket notifications  
  **基于令牌的认证**: 刷新令牌流程和JWT保护的WebSocket通知
- **Redis cache coverage** for hot feed, post detail, unread count, and profile hot paths  
  **Redis缓存覆盖**: 热门信息流、帖子详情、未读数、个人资料热点路径
- **RabbitMQ-based async notification pipeline** with near-real-time WebSocket delivery  
  **基于RabbitMQ的异步通知管道**: 近实时WebSocket推送
- **MinIO-backed avatar and post cover uploads**  
  **MinIO支持的头像和帖子封面上传**
- **CI workflow plus integration tests** for key portfolio-ready flows  
  **CI工作流和集成测试**: 关键作品集就绪流程

## Monorepo Structure | 仓库结构

```text
apps/api    Spring Boot modular monolith backend
apps/web    React + TypeScript user-facing web app
apps/admin  React + TypeScript admin app
deploy      Docker Compose stack for local infrastructure
docs        Architecture, deployment, API, and interview notes
```

## Tech Stack | 技术栈

- **Backend**: Spring Boot 3, Java 17, Spring Security, Spring Data JPA, Flyway
  **后端**: Spring Boot 3, Java 17, Spring Security, Spring Data JPA, Flyway
- **Frontend**: React, TypeScript, Vite
  **前端**: React, TypeScript, Vite
- **Infrastructure**: MySQL, Redis, RabbitMQ, MinIO
  **基础设施**: MySQL, Redis, RabbitMQ, MinIO
- **Realtime**: WebSocket
  **实时**: WebSocket
- **API Docs**: OpenAPI / Swagger UI
  **API文档**: OpenAPI / Swagger UI
- **Tooling**: Docker Compose, GitHub Actions CI
  **工具**: Docker Compose, GitHub Actions CI

## Core Features | 当前已实现功能

- **Authentication**: register, login, refresh token, logout
  **认证**: 注册、登录、刷新令牌、登出
- **User profile**: display name, bio, language preference, avatar upload
  **用户资料**: 显示名称、简介、语言偏好、头像上传
- **Community feed**: latest feed, hot feed, category filter, cursor pagination
  **社区信息流**: 最新信息流、热门信息流、分类过滤、游标分页
- **Search**: keyword and category-based post search with shareable query URLs
  **搜索**: 关键词和分类帖子搜索，支持可分享查询URL
- **Content workflow**: create, edit, delete, detail view, cover upload
  **内容工作流**: 创建、编辑、删除、详情查看、封面上传
- **Social interactions**: like, favorite, comment, follow / unfollow
  **社交互动**: 点赞、收藏、评论、关注/取消关注
- **Notifications**: unread count, list, mark-as-read, WebSocket push
  **通知**: 未读数、列表、标记已读、WebSocket推送
- **Reports and moderation**: report posts/users, review reports, hide posts, disable users
  **举报和审核**: 举报帖子/用户、审核举报、隐藏帖子、禁用用户
- **Admin audit trail**: trace moderation actions with operator, target, and timestamp
  **管理员审计轨迹**: 追踪审核操作，包含操作员、目标和时间戳
- **Bilingual experience**: `en-US` and `zh-CN` across web, admin, and docs
  **双语体验**: web端、管理端和文档均支持中英文

## Architecture Notes | 架构说明

**Backend Architecture:**  
**后端架构:**

- The backend uses a **modular monolith structure** to keep delivery fast while preserving clear module boundaries.  
  **后端采用模块化单体结构**，在保持交付效率的同时保留清晰的模块边界。
- **Read-heavy endpoints use explicit Redis caching** with straightforward invalidation rules.  
  **高频读取接口使用显式Redis缓存**，并配合简单可解释的失效策略。
- **Interaction side effects are decoupled through RabbitMQ** and pushed to clients via WebSocket.  
  **互动副作用通过RabbitMQ解耦**，再由WebSocket推送到前端。
- **The repository is designed around a 300k DAU target mindset**, focusing on practical and explainable engineering tradeoffs.  
  **整个项目按300k DAU的目标思路设计**，强调务实、可解释、适合面试展开的工程取舍。

**Performance Optimizations:**  
**性能优化:**

- **Cursor Pagination**: Avoids N+1 query problems in deep pagination  
  **游标分页**: 避免深度分页中的N+1查询问题
- **Multi-layer Caching**: Redis + application-level caching for hot paths  
  **多层缓存**: Redis + 应用级缓存用于热点路径
- **Database Indexing**: Optimized indexes for high-frequency query patterns  
  **数据库索引**: 为高频查询模式优化的索引
- **Async Processing**: Non-blocking notification pipeline  
  **异步处理**: 非阻塞通知管道

**Scalability Design:**  
**可扩展性设计:**

- **Horizontal Scaling**: Redis cluster + MySQL read replicas ready  
  **水平扩展**: Redis集群 + MySQL读副本就绪
- **Modular Boundaries**: Each module can be deployed independently  
  **模块边界**: 每个模块可独立部署
- **Event-driven Architecture**: Loose coupling via message queues  
  **事件驱动架构**: 通过消息队列松耦合

## Local Quick Start | 本地启动

1. **Install frontend dependencies** at repository root.  
   **在仓库根目录安装前端依赖。**
   ```bash
   npm ci
   ```

2. **Start infrastructure services**.  
   **启动本地基础设施服务。**
   ```bash
   cd deploy
   docker compose up -d mysql redis rabbitmq minio
   ```

3. **Start the backend API**.  
   **启动后端服务。**
   ```bash
   mvn -f apps/api/pom.xml spring-boot:run
   ```

4. **Start the web app**.  
   **启动用户端。**
   ```bash
   npm run dev:web
   ```

5. **Start the admin app**.  
   **启动管理端。**
   ```bash
   npm run dev:admin
   ```

## Local URLs | 本地地址

- **API**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Web**: `http://localhost:5173`
- **Admin**: `http://localhost:5174`
- **RabbitMQ Console**: `http://localhost:15672`
- **MinIO API**: `http://localhost:9000`
- **MinIO Console**: `http://localhost:9001`

## Demo Accounts | 演示账号

- **Admin bootstrap account**: `admin / Admin@123456`
- **Seed users**:
  - `alice@devflow.local`
  - `bob@devflow.local`
  - `carol@devflow.local`
  - `david@devflow.local`
- **Seed password**: `password`

## CI and Quality Signals | CI 与质量信号

- **GitHub Actions** builds both frontend apps and runs backend tests  
  **GitHub Actions**构建两个前端应用并运行后端测试
- **Integration tests** cover auth refresh, profile update, admin moderation, search, report review, and admin audit logs  
  **集成测试**覆盖认证刷新、个人资料更新、管理员审核、搜索、举报审核和管理员审计日志
- **Swagger UI** is enabled for fast API inspection during demos and interviews  
  **Swagger UI**已启用，便于演示和面试期间快速检查API

## Related Docs | 相关文档

- [Docs Index](./docs/README.md)
- [Architecture Diagram](./docs/ARCHITECTURE_DIAGRAM.md)
- [API Overview](./docs/API_OVERVIEW.md)
- [Interview Highlights](./docs/INTERVIEW_HIGHLIGHTS.md)

## Portfolio Positioning | 作品集定位

**This project is intentionally built to be easy to explain in interviews:**  
**这个项目刻意保持"面试可讲清楚"的工程形态：**

- **Clear module boundaries**: Easy to discuss separation of concerns  
  **清晰的模块边界**: 便于讨论关注点分离
- **Realistic product workflows**: Complete user journey from registration to content moderation  
  **真实的产品工作流**: 从注册到内容审核的完整用户旅程
- **Practical infrastructure choices**: Monolith vs microservices, caching strategies, async processing  
  **务实的基础设施选择**: 单体vs微服务、缓存策略、异步处理
- **Enough depth to discuss**: Caching, async pipelines, security, governance, and delivery quality  
  **足够的讨论深度**: 缓存、异步管道、安全、治理和交付质量

**Key Interview Topics:**  
**关键面试话题:**

- **Why modular monolith over microservices?**  
  **为什么选择模块化单体而非微服务？**
- **How do you handle cache invalidation?**  
  **如何处理缓存失效？**
- **Explain your cursor pagination implementation.**  
  **解释你的游标分页实现。**
- **How does the notification pipeline work?**  
  **通知管道是如何工作的？**
- **What are your performance optimization strategies?**  
  **你的性能优化策略是什么？**
- **How do you ensure data consistency?**  
  **如何确保数据一致性？**
