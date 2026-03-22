-- EN: Phase 9 - Atomic counters for high-concurrency scenarios
-- 中文：Phase 9 - 高并发场景的原子计数器

-- 原子计数器表，解决热点更新问题
CREATE TABLE post_counters (
    post_id BIGINT PRIMARY KEY,
    like_count BIGINT NOT NULL DEFAULT 0,
    comment_count BIGINT NOT NULL DEFAULT 0,
    favorite_count BIGINT NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    version BIGINT NOT NULL DEFAULT 0,
    
    INDEX idx_counters_updated (updated_at),
    INDEX idx_counters_hot (like_count, comment_count, favorite_count, updated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 用户计数器表，用于统计和限流
CREATE TABLE user_counters (
    user_id BIGINT PRIMARY KEY,
    posts_count BIGINT NOT NULL DEFAULT 0,
    followers_count BIGINT NOT NULL DEFAULT 0,
    following_count BIGINT NOT NULL DEFAULT 0,
    likes_received_count BIGINT NOT NULL DEFAULT 0,
    comments_received_count BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    version BIGINT NOT NULL DEFAULT 0,
    
    INDEX idx_user_counters_updated (updated_at),
    INDEX idx_user_counters_active (followers_count DESC, posts_count DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 热点帖子缓存表，用于缓存热点数据
CREATE TABLE hot_posts_cache (
    post_id BIGINT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author_id BIGINT NOT NULL,
    author_display_name VARCHAR(64) NOT NULL,
    category_id BIGINT NOT NULL,
    like_count BIGINT NOT NULL DEFAULT 0,
    comment_count BIGINT NOT NULL DEFAULT 0,
    favorite_count BIGINT NOT NULL DEFAULT 0,
    score DECIMAL(15,6) NOT NULL DEFAULT 0,
    published_at TIMESTAMP(3) NOT NULL,
    cached_at TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    
    INDEX idx_hot_cache_score (score DESC, published_at DESC),
    INDEX idx_hot_cache_category (category_id, score DESC, published_at DESC),
    INDEX idx_hot_cache_time (cached_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 初始化现有帖子的计数器
INSERT INTO post_counters (post_id, like_count, comment_count, favorite_count, updated_at)
SELECT 
    id,
    COALESCE(like_count, 0),
    COALESCE(comment_count, 0), 
    COALESCE(favorite_count, 0),
    COALESCE(updated_at, created_at)
FROM posts 
WHERE status = 'PUBLISHED' 
  AND deleted_at IS NULL
ON DUPLICATE KEY UPDATE
    like_count = VALUES(like_count),
    comment_count = VALUES(comment_count),
    favorite_count = VALUES(favorite_count),
    updated_at = VALUES(updated_at);

-- 初始化现有用户的计数器
INSERT INTO user_counters (user_id, posts_count, updated_at)
SELECT 
    id,
    (SELECT COUNT(*) FROM posts WHERE author_id = users.id AND status = 'PUBLISHED' AND deleted_at IS NULL),
    updated_at
FROM users 
WHERE status = 'ACTIVE'
ON DUPLICATE KEY UPDATE
    posts_count = VALUES(posts_count),
    updated_at = VALUES(updated_at);
