CREATE TABLE user_follow (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  follower_id BIGINT NOT NULL,
  following_id BIGINT NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT uq_user_follow UNIQUE (follower_id, following_id),
  CONSTRAINT fk_user_follow_follower FOREIGN KEY (follower_id) REFERENCES users(id),
  CONSTRAINT fk_user_follow_following FOREIGN KEY (following_id) REFERENCES users(id)
);

CREATE TABLE post_likes (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT uq_post_likes_post_user UNIQUE (post_id, user_id),
  CONSTRAINT fk_post_likes_post FOREIGN KEY (post_id) REFERENCES posts(id),
  CONSTRAINT fk_post_likes_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE post_favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT uq_post_favorites_post_user UNIQUE (post_id, user_id),
  CONSTRAINT fk_post_favorites_post FOREIGN KEY (post_id) REFERENCES posts(id),
  CONSTRAINT fk_post_favorites_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE comments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  parent_id BIGINT NULL,
  content VARCHAR(2000) NOT NULL,
  status VARCHAR(16) NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL,
  CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id),
  CONSTRAINT fk_comments_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE notifications (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  receiver_id BIGINT NOT NULL,
  actor_id BIGINT NOT NULL,
  type VARCHAR(32) NOT NULL,
  target_type VARCHAR(32) NOT NULL,
  target_id BIGINT NOT NULL,
  payload_json VARCHAR(2000) NULL,
  is_read TINYINT(1) NOT NULL DEFAULT 0,
  read_at DATETIME(3) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT fk_notifications_receiver FOREIGN KEY (receiver_id) REFERENCES users(id),
  CONSTRAINT fk_notifications_actor FOREIGN KEY (actor_id) REFERENCES users(id)
);

CREATE INDEX idx_user_follow_follower ON user_follow(follower_id, created_at DESC);
CREATE INDEX idx_user_follow_following ON user_follow(following_id, created_at DESC);
CREATE INDEX idx_post_likes_post ON post_likes(post_id, created_at DESC);
CREATE INDEX idx_post_likes_user ON post_likes(user_id, created_at DESC);
CREATE INDEX idx_post_favorites_post ON post_favorites(post_id, created_at DESC);
CREATE INDEX idx_post_favorites_user ON post_favorites(user_id, created_at DESC);
CREATE INDEX idx_comments_post ON comments(post_id, created_at DESC);
CREATE INDEX idx_comments_user ON comments(user_id, created_at DESC);
CREATE INDEX idx_notifications_receiver_unread ON notifications(receiver_id, is_read, created_at DESC);
