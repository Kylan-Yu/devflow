# Architecture Diagram

## System Context | 系统上下文
```mermaid
flowchart LR
  U["Web User"] --> WEB["apps/web (React + Vite)"]
  A["Admin User"] --> ADMIN["apps/admin (React + Vite)"]
  WEB --> API["apps/api (Spring Boot Modular Monolith)"]
  ADMIN --> API
  API --> MYSQL["MySQL"]
  API --> REDIS["Redis"]
  API --> RABBIT["RabbitMQ"]
  API --> MINIO["MinIO"]
  API --> WS["WebSocket Notification Channel"]
  WS --> WEB
```

**EN**
The backend API is the central entry point. Redis reduces pressure on hot read paths, RabbitMQ decouples side effects, and MinIO stores user-uploaded media.

**中文**
后端 API 是系统核心入口。Redis 用于降低热点读路径压力，RabbitMQ 用于解耦副作用链路，MinIO 用于存储用户上传的媒体文件。

## Backend Module View | 后端模块视图
```mermaid
flowchart TB
  COMMON["common"]
  AUTH["auth"]
  USER["user"]
  POST["post"]
  INTERACTION["interaction"]
  FEED["feed"]
  NOTIFICATION["notification"]
  MEDIA["media"]
  REPORT["report"]
  ADMIN["admin"]

  AUTH --> USER
  POST --> USER
  INTERACTION --> POST
  INTERACTION --> USER
  FEED --> POST
  NOTIFICATION --> USER
  INTERACTION --> NOTIFICATION
  MEDIA --> USER
  MEDIA --> POST
  REPORT --> USER
  REPORT --> POST
  COMMON --> AUTH
  COMMON --> USER
  COMMON --> POST
  COMMON --> INTERACTION
  COMMON --> FEED
  COMMON --> NOTIFICATION
  COMMON --> MEDIA
  COMMON --> REPORT
  COMMON --> ADMIN
```

**EN**
The project uses a modular monolith to balance delivery speed, maintainability, and future service extraction.

**中文**
项目采用模块化单体架构，在交付效率、可维护性和未来拆分服务能力之间保持平衡。

## Feed Read Path | Feed 读取链路
```mermaid
sequenceDiagram
  participant Client
  participant API
  participant Redis
  participant MySQL

  Client->>API: GET /api/v1/feed/latest
  API->>Redis: read cache key
  alt Cache hit
    Redis-->>API: cached feed payload
    API-->>Client: response
  else Cache miss
    API->>MySQL: query feed rows
    MySQL-->>API: rows
    API->>Redis: set short-lived cache
    API-->>Client: response
  end
```

## Notification Async Path | 通知异步链路
```mermaid
sequenceDiagram
  participant Client
  participant API
  participant RabbitMQ
  participant Consumer
  participant MySQL
  participant WebSocket

  Client->>API: like / comment / follow request
  API->>MySQL: write interaction in transaction
  API->>RabbitMQ: publish event after commit
  RabbitMQ->>Consumer: deliver event
  Consumer->>MySQL: persist notification
  Consumer->>WebSocket: push unread update
```

## Media Upload Path | 媒体上传链路
```mermaid
sequenceDiagram
  participant Client
  participant API
  participant MinIO

  Client->>API: upload avatar or post cover
  API->>API: validate token, size, and content type
  API->>MinIO: store object
  MinIO-->>API: object path
  API-->>Client: public URL
```

## 300k DAU Design Mindset | 面向 300k DAU 的设计思路
- Optimize read-heavy paths first
- Keep write path correct and simple
- Offload side effects with asynchronous processing
- Preserve clear module seams for future scale-out

- 优先优化读多写少的热点路径
- 保持写链路正确且足够简单
- 用异步处理卸载通知等副作用
- 保留清晰模块边界，便于后续扩展
