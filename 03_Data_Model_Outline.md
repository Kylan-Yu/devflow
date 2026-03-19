# 数据模型草案 / Data Model Outline

## 1. Core Tables / 核心表

### users / 用户表
**Fields / 字段**
- id
- email
- password_hash
- username
- display_name
- avatar_url
- bio
- locale
- status
- created_at
- updated_at

### user_follow / 用户关注关系
- id
- follower_id
- following_id
- created_at

### posts / 帖子表
- id
- author_id
- title
- content
- content_type
- cover_image_url
- category_id
- status
- visibility
- like_count
- comment_count
- favorite_count
- score
- published_at
- created_at
- updated_at
- deleted_at

### post_tags / 帖子标签关系
- id
- post_id
- tag_id

### tags / 标签表
- id
- name
- locale_key
- status

### categories / 分类表
- id
- code
- name_zh
- name_en
- status
- sort_order

### comments / 评论表
- id
- post_id
- user_id
- parent_id
- content
- status
- created_at
- deleted_at

### post_likes / 帖子点赞表
- id
- post_id
- user_id
- created_at

### post_favorites / 帖子收藏表
- id
- post_id
- user_id
- created_at

### notifications / 通知表
- id
- receiver_id
- actor_id
- type
- target_type
- target_id
- payload_json
- is_read
- created_at

### reports / 举报表
- id
- reporter_id
- target_type
- target_id
- reason_code
- detail
- status
- reviewed_by
- reviewed_at
- created_at

### admin_users / 管理员表
- id
- email
- password_hash
- role_code
- status
- created_at

### audit_logs / 审计日志表
- id
- operator_id
- operator_type
- action
- target_type
- target_id
- detail_json
- created_at

---

## 2. Index Suggestions / 索引建议

**English**
- `users(email)` unique
- `users(username)` unique
- `user_follow(follower_id, following_id)` unique
- `posts(author_id, published_at desc)`
- `posts(status, published_at desc)`
- `posts(category_id, published_at desc)`
- `comments(post_id, created_at desc)`
- `post_likes(post_id, user_id)` unique
- `post_favorites(post_id, user_id)` unique
- `notifications(receiver_id, is_read, created_at desc)`
- `reports(status, created_at desc)`

**中文**
- `users(email)` 唯一索引
- `users(username)` 唯一索引
- `user_follow(follower_id, following_id)` 唯一索引
- `posts(author_id, published_at desc)`
- `posts(status, published_at desc)`
- `posts(category_id, published_at desc)`
- `comments(post_id, created_at desc)`
- `post_likes(post_id, user_id)` 唯一索引
- `post_favorites(post_id, user_id)` 唯一索引
- `notifications(receiver_id, is_read, created_at desc)`
- `reports(status, created_at desc)`

---

## 3. Counter Strategy / 计数策略

**English**
For To-C traffic, avoid real-time expensive aggregation on every request.
Store denormalized counters on posts:
- like_count
- comment_count
- favorite_count
- score

Use async/event-driven refresh when possible.

**中文**
To C 场景下，不要在每次请求时做实时重聚合。
在帖子表维护反范式计数字段：
- like_count
- comment_count
- favorite_count
- score

能异步刷新时尽量走事件驱动。

---

## 4. i18n Data Strategy / 国际化数据策略

**English**
For product UI, use frontend i18n resources.  
For domain data:
- categories store `name_zh`, `name_en`
- tags can store locale key
- backend returns locale-neutral codes when possible

**中文**
界面文案使用前端 i18n 资源。  
业务数据建议：
- 分类表存 `name_zh`、`name_en`
- 标签表可存 locale key
- 后端尽量返回中立 code，由前端做展示映射
