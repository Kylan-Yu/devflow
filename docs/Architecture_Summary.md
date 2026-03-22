# Architecture Summary

## High-Level | 高层说明
**EN**
DevFlow uses a monorepo with a modular monolith backend. The design prioritizes fast delivery while preserving clear boundaries for future service extraction.

**中文**
DevFlow 采用 monorepo 和模块化单体后端架构，在保证交付效率的同时保留清晰边界，便于后续按需拆分服务。

## Repository Layout | 仓库布局
```text
apps/api    Spring Boot API
apps/web    User-facing web app
apps/admin  Admin app
deploy      Docker Compose local stack
docs        Architecture, deployment, and interview notes
```

## Backend Modules | 后端模块
- `auth`: user and admin authentication, JWT, refresh flow
- `user`: profile, language preference, avatar URL
- `post`: content CRUD, detail view, category and tag association
- `interaction`: like, favorite, comment, follow
- `feed`: latest and hot feed queries with pagination
- `notification`: unread count, list, async persistence, WebSocket push
- `media`: MinIO-backed avatar and post cover uploads
- `admin`: moderation overview, user status, post status
- `common`: security, error model, caching, trace, shared infra

## Data and Infra | 数据与基础设施
- MySQL: transactional source of truth
- Redis: cache for hot read endpoints
- RabbitMQ: async delivery for notification side effects
- MinIO: object storage for user media
- WebSocket: near-real-time unread notification updates

## Read / Write Strategy | 读写策略
**EN**
- Write path prioritizes business correctness with transactional MySQL updates.
- Read path uses short-lived and explicit caches to reduce repeated database pressure.
- Side effects such as notifications are moved out of the synchronous request path.

**中文**
- 写链路优先保证业务正确性，核心写操作由 MySQL 事务保障。
- 读链路使用短 TTL、显式 key 的缓存策略，降低重复数据库压力。
- 通知等副作用通过异步链路移出同步请求路径。

## Peak-Capable Design Notes | 面向峰值流量的设计说明
- Cache-first optimization on hot read paths
- Cursor pagination for scalable feed reads
- Denormalized counters on interaction-heavy entities
- Event-driven notification pipeline
- Clear module seams for future scale-out when necessary

- 对热点读链路优先做缓存优化
- 使用游标分页支撑信息流查询扩展
- 对高互动实体使用反范式计数
- 用事件驱动链路处理通知副作用
- 保留清晰模块边界，必要时可继续拆分
