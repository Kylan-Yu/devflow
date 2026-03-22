-- =========================================================
-- DevFlow Full Initialization SQL
-- DevFlow 全量初始化 SQL（结构 + 索引 + 演示数据）
-- =========================================================
-- EN: This script is standalone and can be imported directly into MySQL 8+.
-- 中文：本脚本可独立导入到 MySQL 8+，用于一键初始化数据库。

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS devflow
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE devflow;

-- =========================================================
-- Drop existing tables (safe re-import)
-- 删除已有表（支持重复导入）
-- =========================================================
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS post_favorites;
DROP TABLE IF EXISTS post_likes;
DROP TABLE IF EXISTS user_follow;
DROP TABLE IF EXISTS post_tags;
DROP TABLE IF EXISTS posts;
DROP TABLE IF EXISTS tags;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS admin_users;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- =========================================================
-- users
-- 用户表 / Users table
-- =========================================================
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID / User ID',
  username VARCHAR(32) NOT NULL COMMENT '用户名（唯一） / Username (unique)',
  email VARCHAR(128) NOT NULL COMMENT '邮箱（唯一） / Email (unique)',
  password_hash VARCHAR(100) NOT NULL COMMENT '密码哈希 / Password hash',
  display_name VARCHAR(64) NOT NULL COMMENT '显示名称 / Display name',
  bio VARCHAR(255) NULL COMMENT '个人简介 / User bio',
  preferred_language VARCHAR(16) NOT NULL COMMENT '语言偏好：zh-CN 或 en-US / Preferred language: zh-CN or en-US',
  role VARCHAR(16) NOT NULL COMMENT '角色：USER 或 ADMIN / Role: USER or ADMIN',
  status VARCHAR(16) NOT NULL COMMENT '状态：ACTIVE 或 DISABLED / Status: ACTIVE or DISABLED',
  last_login_at DATETIME(3) NULL COMMENT '最后登录时间 / Last login time',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间 / Updated time',
  CONSTRAINT uq_users_username UNIQUE (username),
  CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表 / Users table';

-- =========================================================
-- admin_users
-- 管理员表 / Admin users table
-- =========================================================
CREATE TABLE admin_users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID / Admin ID',
  username VARCHAR(64) NOT NULL COMMENT '管理员用户名（唯一） / Admin username (unique)',
  password_hash VARCHAR(100) NOT NULL COMMENT '管理员密码哈希 / Admin password hash',
  display_name VARCHAR(64) NOT NULL COMMENT '管理员显示名 / Admin display name',
  status VARCHAR(16) NOT NULL COMMENT '管理员状态：ACTIVE 或 DISABLED / Admin status: ACTIVE or DISABLED',
  last_login_at DATETIME(3) NULL COMMENT '最后登录时间 / Last login time',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间 / Updated time',
  CONSTRAINT uq_admin_users_username UNIQUE (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员表 / Admin users table';

-- =========================================================
-- refresh_tokens
-- 刷新令牌表 / Refresh tokens table
-- =========================================================
CREATE TABLE refresh_tokens (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '刷新令牌ID / Refresh token ID',
  principal_type VARCHAR(16) NOT NULL COMMENT '主体类型：USER 或 ADMIN / Principal type: USER or ADMIN',
  subject_id BIGINT NOT NULL COMMENT '主体ID / Subject ID',
  token_hash CHAR(64) NOT NULL COMMENT '令牌哈希（唯一） / Token hash (unique)',
  expires_at DATETIME(3) NOT NULL COMMENT '过期时间 / Expiration time',
  revoked TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否撤销（0否1是） / Revoked flag (0 no, 1 yes)',
  revoked_at DATETIME(3) NULL COMMENT '撤销时间 / Revoked time',
  replaced_by_hash CHAR(64) NULL COMMENT '替换令牌哈希 / Replaced token hash',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  CONSTRAINT uq_refresh_tokens_hash UNIQUE (token_hash),
  INDEX idx_refresh_tokens_subject (principal_type, subject_id),
  INDEX idx_refresh_tokens_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='刷新令牌表 / Refresh tokens table';

-- =========================================================
-- categories
-- 分类表 / Categories table
-- =========================================================
CREATE TABLE categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID / Category ID',
  code VARCHAR(64) NOT NULL COMMENT '分类编码（唯一） / Category code (unique)',
  name_zh VARCHAR(64) NOT NULL COMMENT '中文名称 / Chinese name',
  name_en VARCHAR(64) NOT NULL COMMENT '英文名称 / English name',
  status VARCHAR(16) NOT NULL COMMENT '状态：ACTIVE 或 DISABLED / Status: ACTIVE or DISABLED',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '排序值 / Sort order',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间 / Updated time',
  CONSTRAINT uq_categories_code UNIQUE (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分类表 / Categories table';

-- =========================================================
-- tags
-- 标签表 / Tags table
-- =========================================================
CREATE TABLE tags (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标签ID / Tag ID',
  name VARCHAR(64) NOT NULL COMMENT '标签名称（唯一） / Tag name (unique)',
  locale_key VARCHAR(128) NULL COMMENT '国际化键 / i18n locale key',
  status VARCHAR(16) NOT NULL COMMENT '状态：ACTIVE 或 DISABLED / Status: ACTIVE or DISABLED',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间 / Updated time',
  CONSTRAINT uq_tags_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表 / Tags table';

-- =========================================================
-- posts
-- 帖子表 / Posts table
-- =========================================================
CREATE TABLE posts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子ID / Post ID',
  author_id BIGINT NOT NULL COMMENT '作者用户ID / Author user ID',
  title VARCHAR(160) NOT NULL COMMENT '标题 / Title',
  content TEXT NOT NULL COMMENT '正文内容 / Post content',
  content_type VARCHAR(16) NOT NULL COMMENT '内容类型：MARKDOWN 或 RICH_TEXT / Content type: MARKDOWN or RICH_TEXT',
  cover_image_url VARCHAR(255) NULL COMMENT '封面图URL / Cover image URL',
  category_id BIGINT NOT NULL COMMENT '分类ID / Category ID',
  status VARCHAR(16) NOT NULL COMMENT '状态：PUBLISHED 或 DELETED / Status: PUBLISHED or DELETED',
  visibility VARCHAR(16) NOT NULL COMMENT '可见性：PUBLIC / Visibility: PUBLIC',
  like_count INT NOT NULL DEFAULT 0 COMMENT '点赞数 / Like count',
  comment_count INT NOT NULL DEFAULT 0 COMMENT '评论数 / Comment count',
  favorite_count INT NOT NULL DEFAULT 0 COMMENT '收藏数 / Favorite count',
  score DOUBLE NOT NULL DEFAULT 0 COMMENT '热门分数 / Hot score',
  published_at DATETIME(3) NOT NULL COMMENT '发布时间 / Published time',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间 / Updated time',
  deleted_at DATETIME(3) NULL COMMENT '删除时间（软删除） / Deleted time (soft delete)',
  CONSTRAINT fk_posts_author_id FOREIGN KEY (author_id) REFERENCES users(id),
  CONSTRAINT fk_posts_category_id FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子表 / Posts table';

-- =========================================================
-- post_tags
-- 帖子标签关联表 / Post-tag relation table
-- =========================================================
CREATE TABLE post_tags (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关联ID / Relation ID',
  post_id BIGINT NOT NULL COMMENT '帖子ID / Post ID',
  tag_id BIGINT NOT NULL COMMENT '标签ID / Tag ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  CONSTRAINT uq_post_tags_post_id_tag_id UNIQUE (post_id, tag_id),
  CONSTRAINT fk_post_tags_post_id FOREIGN KEY (post_id) REFERENCES posts(id),
  CONSTRAINT fk_post_tags_tag_id FOREIGN KEY (tag_id) REFERENCES tags(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='帖子标签关联表 / Post-tag relation table';

-- =========================================================
-- user_follow
-- 关注关系表 / User follow table
-- =========================================================
CREATE TABLE user_follow (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关注关系ID / Follow relation ID',
  follower_id BIGINT NOT NULL COMMENT '关注者ID / Follower user ID',
  following_id BIGINT NOT NULL COMMENT '被关注者ID / Following user ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  CONSTRAINT uq_user_follow UNIQUE (follower_id, following_id),
  CONSTRAINT fk_user_follow_follower FOREIGN KEY (follower_id) REFERENCES users(id),
  CONSTRAINT fk_user_follow_following FOREIGN KEY (following_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='关注关系表 / User follow table';

-- =========================================================
-- post_likes
-- 点赞表 / Post likes table
-- =========================================================
CREATE TABLE post_likes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点赞记录ID / Like record ID',
  post_id BIGINT NOT NULL COMMENT '帖子ID / Post ID',
  user_id BIGINT NOT NULL COMMENT '用户ID / User ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  CONSTRAINT uq_post_likes_post_user UNIQUE (post_id, user_id),
  CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts(id),
  CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点赞表 / Post likes table';

-- =========================================================
-- post_favorites
-- 收藏表 / Post favorites table
-- =========================================================
CREATE TABLE post_favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏记录ID / Favorite record ID',
  post_id BIGINT NOT NULL COMMENT '帖子ID / Post ID',
  user_id BIGINT NOT NULL COMMENT '用户ID / User ID',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  CONSTRAINT uq_post_favorites_post_user UNIQUE (post_id, user_id),
  CONSTRAINT fk_post_favorites_post FOREIGN KEY (post_id) REFERENCES posts(id),
  CONSTRAINT fk_post_favorites_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表 / Post favorites table';

-- =========================================================
-- comments
-- 评论表 / Comments table
-- =========================================================
CREATE TABLE comments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID / Comment ID',
  post_id BIGINT NOT NULL COMMENT '帖子ID / Post ID',
  user_id BIGINT NOT NULL COMMENT '评论用户ID / Comment user ID',
  parent_id BIGINT NULL COMMENT '父评论ID（预留楼中楼） / Parent comment ID (reserved)',
  content VARCHAR(2000) NOT NULL COMMENT '评论内容 / Comment content',
  status VARCHAR(16) NOT NULL COMMENT '评论状态：ACTIVE 或 DELETED / Comment status: ACTIVE or DELETED',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  deleted_at DATETIME(3) NULL COMMENT '删除时间 / Deleted time',
  CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id),
  CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表 / Comments table';

-- =========================================================
-- notifications
-- 通知表 / Notifications table
-- =========================================================
CREATE TABLE notifications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID / Notification ID',
  receiver_id BIGINT NOT NULL COMMENT '接收者用户ID / Receiver user ID',
  actor_id BIGINT NOT NULL COMMENT '触发者用户ID / Actor user ID',
  type VARCHAR(32) NOT NULL COMMENT '通知类型：LIKE/COMMENT/FOLLOW / Notification type: LIKE/COMMENT/FOLLOW',
  target_type VARCHAR(32) NOT NULL COMMENT '目标类型：POST/COMMENT/USER / Target type: POST/COMMENT/USER',
  target_id BIGINT NOT NULL COMMENT '目标ID / Target ID',
  payload_json VARCHAR(2000) NULL COMMENT '扩展内容JSON / Payload JSON',
  is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否已读（0否1是） / Read flag (0 no, 1 yes)',
  read_at DATETIME(3) NULL COMMENT '已读时间 / Read time',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间 / Created time',
  CONSTRAINT fk_notifications_receiver FOREIGN KEY (receiver_id) REFERENCES users(id),
  CONSTRAINT fk_notifications_actor FOREIGN KEY (actor_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表 / Notifications table';

-- =========================================================
-- Indexes
-- 索引定义 / Index definitions
-- =========================================================
CREATE INDEX idx_categories_status_sort ON categories(status, sort_order, id);
CREATE INDEX idx_tags_status_name ON tags(status, name);
CREATE INDEX idx_posts_latest_cursor ON posts(status, visibility, published_at, id);
CREATE INDEX idx_posts_hot_cursor ON posts(status, visibility, score, published_at, id);
CREATE INDEX idx_posts_author_latest_cursor ON posts(status, author_id, published_at, id);
CREATE INDEX idx_posts_category_latest_cursor ON posts(status, category_id, published_at, id);
CREATE INDEX idx_post_tags_tag_id_post_id ON post_tags(tag_id, post_id);
CREATE INDEX idx_user_follow_follower ON user_follow(follower_id, created_at DESC);
CREATE INDEX idx_user_follow_following ON user_follow(following_id, created_at DESC);
CREATE INDEX idx_post_likes_post ON post_likes(post_id, created_at DESC);
CREATE INDEX idx_post_likes_user ON post_likes(user_id, created_at DESC);
CREATE INDEX idx_post_favorites_post ON post_favorites(post_id, created_at DESC);
CREATE INDEX idx_post_favorites_user ON post_favorites(user_id, created_at DESC);
CREATE INDEX idx_comments_post ON comments(post_id, created_at DESC);
CREATE INDEX idx_comments_user ON comments(user_id, created_at DESC);
CREATE INDEX idx_notifications_receiver_unread ON notifications(receiver_id, is_read, created_at DESC);
CREATE INDEX idx_posts_latest_v2 ON posts(status, visibility, deleted_at, published_at DESC, id DESC);
CREATE INDEX idx_posts_hot_v2 ON posts(status, visibility, deleted_at, score DESC, published_at DESC, id DESC);
CREATE INDEX idx_comments_post_active ON comments(post_id, status, deleted_at, created_at DESC, id DESC);
CREATE INDEX idx_notifications_unread_fast ON notifications(receiver_id, is_read, id DESC);

-- =========================================================
-- Basic Reference Data Only
-- 基础参考数据（仅分类和标签）
-- =========================================================

-- Base categories / 基础分类
INSERT INTO categories (code, name_zh, name_en, status, sort_order, created_at, updated_at) VALUES
  ('backend', '后端', 'Backend', 'ACTIVE', 1, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3)),
  ('frontend', '前端', 'Frontend', 'ACTIVE', 2, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3)),
  ('cloud', '云原生', 'Cloud', 'ACTIVE', 3, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3)),
  ('ai', '人工智能', 'AI', 'ACTIVE', 4, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3)),
  ('career', '职业发展', 'Career', 'ACTIVE', 5, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3));

-- Base tags / 基础标签
INSERT INTO tags (name, locale_key, status, created_at, updated_at) VALUES
  ('java', 'tag.java', 'ACTIVE', CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3)),
  ('spring-boot', 'tag.spring_boot', 'ACTIVE', CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3)),
  ('react', 'tag.react', 'ACTIVE', CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3)),
  ('typescript', 'tag.typescript', 'ACTIVE', CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3)),
  ('redis', 'tag.redis', 'ACTIVE', CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3)),
  ('docker', 'tag.docker', 'ACTIVE', CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3));

-- =========================================================
-- Notes
-- 说明
-- =========================================================
-- EN: No demo data included. All user data, posts, comments, likes, etc.
-- should be created through the application interface.
-- 中文：不包含演示数据。所有用户数据、帖子、评论、点赞等都应通过应用界面创建。
-- =========================================================

-- =========================================================
-- End
-- 结束
-- =========================================================
