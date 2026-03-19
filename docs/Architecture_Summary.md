# Architecture Summary | 架构简述

## 1. High-Level
**EN**  
DevFlow uses a monorepo with a modular monolith backend. The architecture favors fast delivery first, while keeping clear module boundaries for future service split.

**中文**  
DevFlow 采用 Monorepo + 模块化单体后端，优先保证交付效率，同时保持清晰模块边界，便于后续按需拆分。

## 2. Repository Layout
```text
apps/api    -> Spring Boot API
apps/web    -> User-facing web app
apps/admin  -> Admin app
deploy      -> Docker Compose local stack
shared      -> shared i18n/docs conventions
docs        -> architecture/deployment/performance docs
```

## 3. Backend Modules
- `auth`: user/admin authentication and token lifecycle
- `user`: profile and language preference
- `post`: content CRUD and detail view
- `interaction`: like/favorite/comment/follow
- `feed`: latest/hot feeds and pagination
- `notification`: async notification persistence + unread count + websocket push
- `common`: cross-cutting concerns (security, error model, trace)

## 4. Data & Infra
- MySQL: transactional source of truth
- Redis: read-path cache for hot endpoints
- RabbitMQ: async event delivery for interactions/notifications
- MinIO: object storage placeholder for media
- WebSocket: near-real-time notification push

## 5. Read/Write Strategy
**EN**
- Write path keeps business correctness first (transactional writes in MySQL).
- Read path uses short and explicit caches to reduce repeated DB pressure.
- Async event flow offloads non-critical work from request latency path.

**中文**
- 写链路优先保证业务正确性（MySQL 事务写入）。
- 读链路通过短 TTL、显式 key 的缓存降低重复数据库压力。
- 通过异步事件把非核心实时任务从请求主链路中解耦。

## 6. Peak-Capable Design Notes (300k DAU target mindset)
- Cache-first optimization on high-frequency reads
- Cursor pagination for feed scalability
- Denormalized counters for interaction-heavy entities
- Async notifications and event-driven side effects
- Clear modular seams to split services later if needed
