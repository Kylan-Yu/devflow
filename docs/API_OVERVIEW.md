# API Overview

## API Style | API 风格
**EN**
- Base path: `/api/v1`
- Unified response model: `code`, `message`, `data`, `traceId`
- Backend returns stable message codes and the frontend maps localized text

**中文**
- 基础路径：`/api/v1`
- 统一响应结构：`code`、`message`、`data`、`traceId`
- 后端返回稳定的 message code，前端负责本地化文案映射

## User-Facing APIs | 用户侧接口

### Auth
- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/logout`

### User Profile
- `GET /users/{id}`
- `GET /users/me`
- `PUT /users/me`
- `POST /users/{id}/follow`
- `DELETE /users/{id}/follow`

### Feed and Posts
- `GET /feed/latest`
- `GET /feed/hot`
- `GET /search/posts`
- `POST /posts`
- `PUT /posts/{id}`
- `DELETE /posts/{id}`
- `GET /posts/{id}`
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
- WebSocket: `/ws/notifications?token={accessToken}`

### Media
- `POST /media/avatar`
- `POST /media/post-cover`

### Report
- `POST /posts/{id}/reports`
- `POST /users/{id}/reports`
- `GET /reports/me`

## Admin APIs | 管理端接口
- `POST /admin/auth/login`
- `GET /admin/overview`
- `GET /admin/users`
- `PATCH /admin/users/{id}/status`
- `GET /admin/posts`
- `PATCH /admin/posts/{id}/status`
- `GET /admin/reports`
- `PATCH /admin/reports/{id}`
- `GET /admin/audit-logs`

## Caching Coverage | 缓存覆盖范围
**EN**
- Cached endpoints:
  - `GET /feed/latest` first page
  - `GET /feed/hot` first page
  - `GET /posts/{id}`
  - `GET /notifications/unread-count`
  - `GET /users/{id}`
  - `GET /users/me`
- Cache keys use the prefix `devflow:cache:*`

**中文**
- 已缓存接口：
  - `GET /feed/latest` 首屏
  - `GET /feed/hot` 首屏
  - `GET /posts/{id}`
  - `GET /notifications/unread-count`
  - `GET /users/{id}`
  - `GET /users/me`
- 缓存 key 统一使用前缀 `devflow:cache:*`

## Sync vs Async | 同步与异步边界
**EN**
- Sync: auth, profile, feed, post CRUD, search, report submission, and moderation reads/writes
- Async: interaction-to-notification side effects through RabbitMQ plus WebSocket push

**中文**
- 同步：认证、资料、信息流、搜索、帖子 CRUD、举报提交和后台治理读写
- 异步：互动行为触发通知副作用，通过 RabbitMQ 和 WebSocket 链路完成

## OpenAPI Entry | OpenAPI 入口
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
