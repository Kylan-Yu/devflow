# Performance Notes | 性能说明

## 1. Targeted Paths | 本阶段重点链路
- Feed list (`/api/v1/feed/latest`, `/api/v1/feed/hot`)
- Post detail (`/api/v1/posts/{id}`)
- Notification unread count (`/api/v1/notifications/unread-count`)
- User profile basic info (`/api/v1/users/{id}`, `/api/v1/users/me`)

## 2. Cache Strategy | 缓存策略
### Key naming
- Prefix: `devflow:cache:`
- Feed first page: `devflow:cache:feed:{latest|hot}:size:{n}:category:{id|all}`
- Post detail: `devflow:cache:post:detail:{postId}`
- Unread count: `devflow:cache:notification:unread:{userId}`
- User profile: `devflow:cache:user:profile:{userId}`

### TTL and invalidation
- Feed first page:
  - TTL: 45s
  - Invalidate on post write and interaction counter changes
- Post detail:
  - TTL: 5m
  - Invalidate on post edit/delete and interaction counter updates
- Unread count:
  - TTL: 30s
  - Invalidate/update on notification create/read actions
- User profile:
  - TTL: 10m
  - Invalidate on profile update

## 3. SQL/Index Notes | SQL/索引优化说明
- Added composite indexes for latest/hot feed with `deleted_at` included.
- Added comment list index optimized for active-comment query on post detail page.
- Added unread count oriented index on notifications.
- Hot feed query now sorts by persisted `score` (index-friendly) instead of re-computing expression on each row.

## 4. Sync vs Async | 同步与异步边界
**Sync**
- Post/feed/profile/detail/unread APIs return from synchronous request-response flow.
- Core writes (post/interactions) are transactional in MySQL.

**Async**
- Interaction side effects to notification are published via RabbitMQ after commit.
- Notification persistence consumer and WebSocket push run asynchronously.

## 5. Portfolio-Friendly Tradeoffs | 作品集可解释性取舍
- Keep cache logic explicit and readable.
- Avoid complex multi-layer cache orchestration.
- Prefer clear invalidation over aggressive micro-optimizations.
