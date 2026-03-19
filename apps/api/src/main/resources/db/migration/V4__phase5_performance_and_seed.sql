-- EN: Phase 5 performance indexes for high-frequency read paths.
-- 中文：Phase 5 针对高频读链路补充索引。
CREATE INDEX idx_posts_latest_v2 ON posts(status, visibility, deleted_at, published_at DESC, id DESC);
CREATE INDEX idx_posts_hot_v2 ON posts(status, visibility, deleted_at, score DESC, published_at DESC, id DESC);
CREATE INDEX idx_comments_post_active ON comments(post_id, status, deleted_at, created_at DESC, id DESC);
CREATE INDEX idx_notifications_unread_fast ON notifications(receiver_id, is_read, id DESC);

-- EN: Demo users for local showcase.
-- 中文：本地演示用户数据。
INSERT INTO users (username, email, password_hash, display_name, bio, preferred_language, role, status, last_login_at, created_at, updated_at)
SELECT
  'alice',
  'alice@devflow.local',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA8Vw2J1rYkWwaRM42QikIze8ih/7G',
  'Alice Chen',
  'Backend engineer, API and architecture enthusiast.',
  'en-US',
  'USER',
  'ACTIVE',
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'alice@devflow.local');

INSERT INTO users (username, email, password_hash, display_name, bio, preferred_language, role, status, last_login_at, created_at, updated_at)
SELECT
  'bob',
  'bob@devflow.local',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA8Vw2J1rYkWwaRM42QikIze8ih/7G',
  'Bob Liu',
  'Frontend engineer focused on React performance.',
  'zh-CN',
  'USER',
  'ACTIVE',
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'bob@devflow.local');

INSERT INTO users (username, email, password_hash, display_name, bio, preferred_language, role, status, last_login_at, created_at, updated_at)
SELECT
  'carol',
  'carol@devflow.local',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA8Vw2J1rYkWwaRM42QikIze8ih/7G',
  'Carol Wang',
  'Cloud-native learner and DevOps practitioner.',
  'en-US',
  'USER',
  'ACTIVE',
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'carol@devflow.local');

INSERT INTO users (username, email, password_hash, display_name, bio, preferred_language, role, status, last_login_at, created_at, updated_at)
SELECT
  'david',
  'david@devflow.local',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA8Vw2J1rYkWwaRM42QikIze8ih/7G',
  'David Sun',
  'AI product builder and side-project founder.',
  'zh-CN',
  'USER',
  'ACTIVE',
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3)
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'david@devflow.local');

-- EN: Demo posts.
-- 中文：本地演示帖子数据。
INSERT INTO posts (
  author_id, title, content, content_type, cover_image_url, category_id, status, visibility,
  like_count, comment_count, favorite_count, score, published_at, created_at, updated_at, deleted_at
)
SELECT
  u.id,
  'How I tuned Spring Boot API latency under 250ms p95',
  'I benchmarked request hotspots, added cursor pagination and introduced short TTL cache for feed first page.',
  'MARKDOWN',
  NULL,
  c.id,
  'PUBLISHED',
  'PUBLIC',
  18,
  6,
  9,
  64.200000,
  DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 36 HOUR),
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3),
  NULL
FROM users u
JOIN categories c ON c.code = 'backend'
WHERE u.email = 'alice@devflow.local'
  AND NOT EXISTS (SELECT 1 FROM posts WHERE title = 'How I tuned Spring Boot API latency under 250ms p95');

INSERT INTO posts (
  author_id, title, content, content_type, cover_image_url, category_id, status, visibility,
  like_count, comment_count, favorite_count, score, published_at, created_at, updated_at, deleted_at
)
SELECT
  u.id,
  'React list rendering checklist for feed pages',
  'Use stable keys, avoid unnecessary re-renders, and cache only first-page feed payload for high ROI.',
  'MARKDOWN',
  NULL,
  c.id,
  'PUBLISHED',
  'PUBLIC',
  26,
  8,
  11,
  82.500000,
  DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 24 HOUR),
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3),
  NULL
FROM users u
JOIN categories c ON c.code = 'frontend'
WHERE u.email = 'bob@devflow.local'
  AND NOT EXISTS (SELECT 1 FROM posts WHERE title = 'React list rendering checklist for feed pages');

INSERT INTO posts (
  author_id, title, content, content_type, cover_image_url, category_id, status, visibility,
  like_count, comment_count, favorite_count, score, published_at, created_at, updated_at, deleted_at
)
SELECT
  u.id,
  'Redis caching playbook for portfolio projects',
  'Keep key naming explicit, TTL short for volatile data, and fail safe when cache is unavailable.',
  'MARKDOWN',
  NULL,
  c.id,
  'PUBLISHED',
  'PUBLIC',
  31,
  10,
  14,
  95.100000,
  DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 16 HOUR),
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3),
  NULL
FROM users u
JOIN categories c ON c.code = 'cloud'
WHERE u.email = 'carol@devflow.local'
  AND NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Redis caching playbook for portfolio projects');

INSERT INTO posts (
  author_id, title, content, content_type, cover_image_url, category_id, status, visibility,
  like_count, comment_count, favorite_count, score, published_at, created_at, updated_at, deleted_at
)
SELECT
  u.id,
  'Designing notification pipelines with RabbitMQ',
  'Publish after commit, consume asynchronously, persist notifications, then push unread updates to websocket clients.',
  'MARKDOWN',
  NULL,
  c.id,
  'PUBLISHED',
  'PUBLIC',
  22,
  7,
  8,
  71.400000,
  DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 10 HOUR),
  CURRENT_TIMESTAMP(3),
  CURRENT_TIMESTAMP(3),
  NULL
FROM users u
JOIN categories c ON c.code = 'backend'
WHERE u.email = 'david@devflow.local'
  AND NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Designing notification pipelines with RabbitMQ');

-- EN: Demo tag mapping.
-- 中文：演示标签映射。
INSERT INTO post_tags (post_id, tag_id, created_at)
SELECT p.id, t.id, CURRENT_TIMESTAMP(3)
FROM posts p
JOIN tags t ON t.name = 'spring-boot'
WHERE p.title = 'How I tuned Spring Boot API latency under 250ms p95'
  AND NOT EXISTS (
    SELECT 1 FROM post_tags x WHERE x.post_id = p.id AND x.tag_id = t.id
  );

INSERT INTO post_tags (post_id, tag_id, created_at)
SELECT p.id, t.id, CURRENT_TIMESTAMP(3)
FROM posts p
JOIN tags t ON t.name = 'react'
WHERE p.title = 'React list rendering checklist for feed pages'
  AND NOT EXISTS (
    SELECT 1 FROM post_tags x WHERE x.post_id = p.id AND x.tag_id = t.id
  );

INSERT INTO post_tags (post_id, tag_id, created_at)
SELECT p.id, t.id, CURRENT_TIMESTAMP(3)
FROM posts p
JOIN tags t ON t.name = 'redis'
WHERE p.title = 'Redis caching playbook for portfolio projects'
  AND NOT EXISTS (
    SELECT 1 FROM post_tags x WHERE x.post_id = p.id AND x.tag_id = t.id
  );

INSERT INTO post_tags (post_id, tag_id, created_at)
SELECT p.id, t.id, CURRENT_TIMESTAMP(3)
FROM posts p
JOIN tags t ON t.name = 'docker'
WHERE p.title = 'Redis caching playbook for portfolio projects'
  AND NOT EXISTS (
    SELECT 1 FROM post_tags x WHERE x.post_id = p.id AND x.tag_id = t.id
  );

INSERT INTO post_tags (post_id, tag_id, created_at)
SELECT p.id, t.id, CURRENT_TIMESTAMP(3)
FROM posts p
JOIN tags t ON t.name = 'java'
WHERE p.title = 'Designing notification pipelines with RabbitMQ'
  AND NOT EXISTS (
    SELECT 1 FROM post_tags x WHERE x.post_id = p.id AND x.tag_id = t.id
  );

-- EN: Demo comments.
-- 中文：演示评论数据。
INSERT INTO comments (post_id, user_id, parent_id, content, status, created_at, deleted_at)
SELECT
  p.id,
  u.id,
  NULL,
  'Great write-up. The cache key design is clear and practical.',
  'ACTIVE',
  DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 8 HOUR),
  NULL
FROM posts p
JOIN users u ON u.email = 'bob@devflow.local'
WHERE p.title = 'How I tuned Spring Boot API latency under 250ms p95'
  AND NOT EXISTS (
    SELECT 1 FROM comments c
    WHERE c.post_id = p.id AND c.user_id = u.id AND c.content = 'Great write-up. The cache key design is clear and practical.'
  );

INSERT INTO comments (post_id, user_id, parent_id, content, status, created_at, deleted_at)
SELECT
  p.id,
  u.id,
  NULL,
  'I tested similar strategy and got meaningful DB load reduction.',
  'ACTIVE',
  DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 6 HOUR),
  NULL
FROM posts p
JOIN users u ON u.email = 'alice@devflow.local'
WHERE p.title = 'Redis caching playbook for portfolio projects'
  AND NOT EXISTS (
    SELECT 1 FROM comments c
    WHERE c.post_id = p.id AND c.user_id = u.id AND c.content = 'I tested similar strategy and got meaningful DB load reduction.'
  );

-- EN: Demo likes and favorites.
-- 中文：演示点赞与收藏数据。
INSERT INTO post_likes (post_id, user_id, created_at)
SELECT p.id, u.id, DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 4 HOUR)
FROM posts p
JOIN users u ON u.email = 'carol@devflow.local'
WHERE p.title = 'How I tuned Spring Boot API latency under 250ms p95'
  AND NOT EXISTS (
    SELECT 1 FROM post_likes x WHERE x.post_id = p.id AND x.user_id = u.id
  );

INSERT INTO post_favorites (post_id, user_id, created_at)
SELECT p.id, u.id, DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 3 HOUR)
FROM posts p
JOIN users u ON u.email = 'david@devflow.local'
WHERE p.title = 'React list rendering checklist for feed pages'
  AND NOT EXISTS (
    SELECT 1 FROM post_favorites x WHERE x.post_id = p.id AND x.user_id = u.id
  );

-- EN: Demo notifications for unread count showcase.
-- 中文：用于未读数演示的通知数据。
INSERT INTO notifications (receiver_id, actor_id, type, target_type, target_id, payload_json, is_read, read_at, created_at)
SELECT
  receiver.id,
  actor.id,
  'LIKE',
  'POST',
  p.id,
  '{"messageCode":"notification.like_received","preview":"How I tuned Spring Boot API latency under 250ms p95"}',
  0,
  NULL,
  DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 2 HOUR)
FROM users receiver
JOIN users actor ON actor.email = 'bob@devflow.local'
JOIN posts p ON p.title = 'How I tuned Spring Boot API latency under 250ms p95'
WHERE receiver.email = 'alice@devflow.local'
  AND NOT EXISTS (
    SELECT 1 FROM notifications n
    WHERE n.receiver_id = receiver.id
      AND n.actor_id = actor.id
      AND n.type = 'LIKE'
      AND n.target_id = p.id
  );

INSERT INTO notifications (receiver_id, actor_id, type, target_type, target_id, payload_json, is_read, read_at, created_at)
SELECT
  receiver.id,
  actor.id,
  'COMMENT',
  'POST',
  p.id,
  '{"messageCode":"notification.comment_received","preview":"Great write-up. The cache key design is clear and practical."}',
  0,
  NULL,
  DATE_SUB(CURRENT_TIMESTAMP(3), INTERVAL 1 HOUR)
FROM users receiver
JOIN users actor ON actor.email = 'carol@devflow.local'
JOIN posts p ON p.title = 'How I tuned Spring Boot API latency under 250ms p95'
WHERE receiver.email = 'alice@devflow.local'
  AND NOT EXISTS (
    SELECT 1 FROM notifications n
    WHERE n.receiver_id = receiver.id
      AND n.actor_id = actor.id
      AND n.type = 'COMMENT'
      AND n.target_id = p.id
  );
