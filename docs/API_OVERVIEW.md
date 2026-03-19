# API Overview | API 总览

## 1. API Style | API 风格
**EN**
- Base path: `/api/v1`
- Unified response model:
  - `code`
  - `message`
  - `data`
  - `traceId`
- Backend returns stable message codes; frontend maps localized text.

**中文**
- 基础路径：`/api/v1`
- 统一响应结构：
  - `code`
  - `message`
  - `data`
  - `traceId`
- 后端返回稳定 message code，前端负责本地化文案映射。

## 2. Public/User APIs | 用户侧 API
### Auth
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`

### User
- `GET /users/{id}`
- `GET /users/me`
- `PUT /users/me`
- `POST /users/{id}/follow`
- `DELETE /users/{id}/follow`

### Post / Feed
- `POST /posts`
- `PUT /posts/{id}`
- `DELETE /posts/{id}`
- `GET /posts/{id}`
- `GET /feed/latest`
- `GET /feed/hot`
- `GET /users/{id}/posts`

### Interaction
- `POST /posts/{id}/likes`
- `DELETE /posts/{id}/likes`
- `POST /posts/{id}/favorites`
- `DELETE /posts/{id}/favorites`
- `GET /posts/{id}/comments`
- `POST /posts/{id}/comments`
- `DELETE /comments/{id}`

### Notification
- `GET /notifications`
- `GET /notifications/unread-count`
- `PATCH /notifications/{id}/read`
- `PATCH /notifications/read-all`
- WebSocket: `/ws/notifications?userId={id}`

## 3. Admin APIs | 管理端 API
- `POST /admin/auth/login`
- More moderation/report/admin APIs are planned in next phase.
- 下一阶段将完善审核、举报处理与管理端更多能力。

## 4. Caching Coverage | 缓存覆盖范围
**EN**
- Cached endpoints:
  - `GET /feed/latest` (first page)
  - `GET /feed/hot` (first page)
  - `GET /posts/{id}`
  - `GET /notifications/unread-count`
  - `GET /users/{id}`
  - `GET /users/me`
- Cache keys follow prefix: `devflow:cache:*`

**中文**
- 已缓存接口：
  - `GET /feed/latest`（首屏）
  - `GET /feed/hot`（首屏）
  - `GET /posts/{id}`
  - `GET /notifications/unread-count`
  - `GET /users/{id}`
  - `GET /users/me`
- 缓存 key 统一前缀：`devflow:cache:*`

## 5. Sync vs Async | 同步与异步边界
**EN**
- Sync: core CRUD and read APIs.
- Async: interaction-to-notification side effects via RabbitMQ + consumer + WebSocket push.

**中文**
- 同步：核心读写与查询接口。  
- 异步：互动后通知副作用链路（RabbitMQ + 消费者 + WebSocket 推送）。  

## 6. OpenAPI Entry | OpenAPI 入口
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
