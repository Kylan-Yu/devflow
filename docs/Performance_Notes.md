# Performance Notes

## Targeted Paths | 当前重点优化链路
- Feed list: `/api/v1/feed/latest`, `/api/v1/feed/hot`
- Post detail: `/api/v1/posts/{id}`
- Notification unread count: `/api/v1/notifications/unread-count`
- User profile basics: `/api/v1/users/{id}`, `/api/v1/users/me`

## Cache Strategy | 缓存策略
### Key Naming
- Prefix: `devflow:cache:`
- Feed first page: `devflow:cache:feed:{latest|hot}:size:{n}:category:{id|all}`
- Post detail: `devflow:cache:post:detail:{postId}`
- Unread count: `devflow:cache:notification:unread:{userId}`
- User profile: `devflow:cache:user:profile:{userId}`

### TTL and Invalidation
- Feed first page:
  - TTL: 45s
  - Invalidate on post write and interaction counter updates
- Post detail:
  - TTL: 5m
  - Invalidate on post edit, delete, and interaction counter changes
- Unread count:
  - TTL: 30s
  - Invalidate on notification create, read, and read-all actions
- User profile:
  - TTL: 10m
  - Invalidate on profile update and moderation status changes

## SQL and Index Notes | SQL 与索引说明
- Composite indexes support latest and hot feed queries while filtering deleted rows
- Comment list index is optimized for active comments on post detail
- Notification unread count uses a read-oriented index
- Hot feed sorting uses persisted `score` instead of re-computing expressions on every row

## Sync vs Async | 同步与异步边界
**Sync**
- Auth, feed, post detail, profile, and moderation APIs use synchronous request-response flow
- Core writes such as post creation and interaction updates remain transactional in MySQL

**Async**
- Interaction side effects publish events to RabbitMQ after commit
- Notification persistence and WebSocket unread push happen asynchronously

## Portfolio-Friendly Tradeoffs | 面试友好的工程取舍
- Keep cache logic explicit and readable
- Avoid over-engineered multi-layer caching
- Prefer predictable invalidation over aggressive micro-optimizations
- Optimize the paths that matter most for a read-heavy community app
