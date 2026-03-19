# 模块边界与 API 范围 / Module Boundaries & API Scope

## 1. Backend Modules / 后端模块

### auth
- register
- login
- refresh token
- logout
- forgot password
- reset password

### user
- profile read/update
- avatar upload
- follow/unfollow
- followers/following list

### post
- create/edit/delete post
- post detail
- author post list
- category/tag query

### interaction
- like/unlike
- favorite/unfavorite
- comment create/delete
- report create

### feed
- latest feed
- hot feed
- following feed
- trending tags

### notification
- notification list
- unread count
- mark read
- websocket push

### admin
- admin auth
- moderation APIs
- report processing
- dashboard metrics
- audit log

---

## 2. Example API List / 示例 API 列表

### Public/User APIs
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `GET /api/v1/users/{id}`
- `PUT /api/v1/users/me`
- `POST /api/v1/users/{id}/follow`
- `DELETE /api/v1/users/{id}/follow`
- `POST /api/v1/posts`
- `PUT /api/v1/posts/{id}`
- `DELETE /api/v1/posts/{id}`
- `GET /api/v1/posts/{id}`
- `GET /api/v1/feed/latest`
- `GET /api/v1/feed/hot`
- `GET /api/v1/feed/following`
- `POST /api/v1/posts/{id}/likes`
- `DELETE /api/v1/posts/{id}/likes`
- `POST /api/v1/posts/{id}/favorites`
- `DELETE /api/v1/posts/{id}/favorites`
- `POST /api/v1/posts/{id}/comments`
- `GET /api/v1/search/posts`
- `GET /api/v1/search/users`
- `GET /api/v1/notifications`
- `GET /api/v1/notifications/unread-count`
- `PATCH /api/v1/notifications/read`

### Admin APIs
- `POST /api/v1/admin/auth/login`
- `GET /api/v1/admin/dashboard`
- `GET /api/v1/admin/posts`
- `PATCH /api/v1/admin/posts/{id}/review`
- `GET /api/v1/admin/comments`
- `PATCH /api/v1/admin/comments/{id}/review`
- `GET /api/v1/admin/reports`
- `PATCH /api/v1/admin/reports/{id}/resolve`
- `GET /api/v1/admin/audit-logs`

---

## 3. API Response Standard / 接口响应标准

**English**
Use a standard structure:
- code
- message
- data
- traceId

**中文**
统一响应结构：
- code
- message
- data
- traceId

**Message Strategy / message 策略**
- backend returns stable message code
- frontend maps localized copy

后端返回稳定 message code，前端负责本地化文案映射。
