# Architecture Diagram | 架构图说明

## 1. System Context | 系统上下文
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
  API --> WS["WebSocket Channel"]
  WS --> WEB
```

**EN**  
The API is the central backend entry, while Redis and RabbitMQ are used to optimize read latency and decouple asynchronous side effects.

**中文**  
API 是后端核心入口，Redis 用于降低高频读延迟，RabbitMQ 用于解耦异步副作用处理。

## 2. Backend Module View | 后端模块视图
```mermaid
flowchart TB
  COMMON["common"]
  AUTH["auth"]
  USER["user"]
  POST["post"]
  INTERACTION["interaction"]
  FEED["feed"]
  NOTIFICATION["notification"]
  ADMIN["admin"]

  AUTH --> USER
  POST --> USER
  INTERACTION --> POST
  INTERACTION --> USER
  FEED --> POST
  NOTIFICATION --> USER
  INTERACTION --> NOTIFICATION
  COMMON --> AUTH
  COMMON --> USER
  COMMON --> POST
  COMMON --> INTERACTION
  COMMON --> FEED
  COMMON --> NOTIFICATION
  COMMON --> ADMIN
```

**EN**  
The project keeps a modular monolith shape to balance delivery speed and future service-splitting readiness.

**中文**  
项目保持模块化单体形态，在交付效率与后续服务拆分之间取得平衡。

## 3. Feed Read Path (Cached) | Feed 读取链路（含缓存）
```mermaid
sequenceDiagram
  participant Client
  participant API
  participant Redis
  participant MySQL

  Client->>API: GET /api/v1/feed/latest (first page)
  API->>Redis: read cache key
  alt Cache hit
    Redis-->>API: cached feed payload
    API-->>Client: response
  else Cache miss
    API->>MySQL: query feed rows
    MySQL-->>API: rows
    API->>Redis: set cache (short TTL)
    API-->>Client: response
  end
```

## 4. Notification Async Path | 通知异步链路
```mermaid
sequenceDiagram
  participant Client
  participant API
  participant RabbitMQ
  participant Consumer
  participant MySQL
  participant WebSocket

  Client->>API: like/comment/follow request
  API->>MySQL: write interaction (transaction)
  API->>RabbitMQ: publish event after commit
  RabbitMQ->>Consumer: deliver event
  Consumer->>MySQL: persist notification
  Consumer->>WebSocket: push unread update
```

## 5. 300k DAU Design Mindset | 30 万 DAU 设计目标思路
**EN**
- Optimize read-heavy paths first
- Keep write path correctness and simplicity
- Use asynchronous processing for side effects
- Keep module seams clear for future scale-out

**中文**
- 优先优化读多写少链路  
- 保持写路径正确性与简洁性  
- 副作用通过异步处理解耦  
- 保持模块边界清晰，便于后续扩展  
