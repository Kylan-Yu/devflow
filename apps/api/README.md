# DevFlow API

## Stack
- Spring Boot 3 + Java 21
- Spring Security + JWT
- Spring Data JPA + Flyway
- Redis + RabbitMQ + WebSocket
- OpenAPI/Swagger

## Phase 5 Focus
- Redis cache integration for high-frequency read paths
- SQL/index optimization for feed and notification
- Demo seed data for local showcase
- OpenAPI documentation completion

## Cached APIs (Phase 5)
- `GET /api/v1/feed/latest` (first page)
- `GET /api/v1/feed/hot` (first page)
- `GET /api/v1/posts/{id}`
- `GET /api/v1/notifications/unread-count`
- `GET /api/v1/users/{id}`
- `GET /api/v1/users/me`

## Cache Key Naming
- Prefix: `devflow:cache:`
- Feed: `devflow:cache:feed:{latest|hot}:size:{n}:category:{id|all}`
- Post detail: `devflow:cache:post:detail:{postId}`
- Unread count: `devflow:cache:notification:unread:{userId}`
- User profile: `devflow:cache:user:profile:{userId}`

## Cache Invalidation Strategy
- Feed first-page cache:
  - TTL 45s
  - Evict on post create/update/delete and interaction counter changes
- Post detail cache:
  - TTL 5m
  - Evict on post updates and interaction counter changes
- Notification unread cache:
  - TTL 30s
  - Evict/update on notification create and read operations
- User profile cache:
  - TTL 10m
  - Evict on profile update

## API Docs
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
