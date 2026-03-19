CREATE TABLE categories (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(64) NOT NULL,
  name_zh VARCHAR(64) NOT NULL,
  name_en VARCHAR(64) NOT NULL,
  status VARCHAR(16) NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT uq_categories_code UNIQUE (code)
);

CREATE TABLE tags (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(64) NOT NULL,
  locale_key VARCHAR(128) NULL,
  status VARCHAR(16) NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT uq_tags_name UNIQUE (name)
);

CREATE TABLE posts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  author_id BIGINT NOT NULL,
  title VARCHAR(160) NOT NULL,
  content TEXT NOT NULL,
  content_type VARCHAR(16) NOT NULL,
  cover_image_url VARCHAR(255) NULL,
  category_id BIGINT NOT NULL,
  status VARCHAR(16) NOT NULL,
  visibility VARCHAR(16) NOT NULL,
  like_count INT NOT NULL DEFAULT 0,
  comment_count INT NOT NULL DEFAULT 0,
  favorite_count INT NOT NULL DEFAULT 0,
  score DOUBLE NOT NULL DEFAULT 0,
  published_at DATETIME(3) NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  deleted_at DATETIME(3) NULL,
  CONSTRAINT fk_posts_author_id FOREIGN KEY (author_id) REFERENCES users(id),
  CONSTRAINT fk_posts_category_id FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE post_tags (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT uq_post_tags_post_id_tag_id UNIQUE (post_id, tag_id),
  CONSTRAINT fk_post_tags_post_id FOREIGN KEY (post_id) REFERENCES posts(id),
  CONSTRAINT fk_post_tags_tag_id FOREIGN KEY (tag_id) REFERENCES tags(id)
);

CREATE INDEX idx_categories_status_sort ON categories(status, sort_order, id);
CREATE INDEX idx_tags_status_name ON tags(status, name);
CREATE INDEX idx_posts_latest_cursor ON posts(status, visibility, published_at, id);
CREATE INDEX idx_posts_hot_cursor ON posts(status, visibility, score, published_at, id);
CREATE INDEX idx_posts_author_latest_cursor ON posts(status, author_id, published_at, id);
CREATE INDEX idx_posts_category_latest_cursor ON posts(status, category_id, published_at, id);
CREATE INDEX idx_post_tags_tag_id_post_id ON post_tags(tag_id, post_id);

INSERT INTO categories (code, name_zh, name_en, status, sort_order) VALUES
  ('backend', '后端', 'Backend', 'ACTIVE', 1),
  ('frontend', '前端', 'Frontend', 'ACTIVE', 2),
  ('cloud', '云原生', 'Cloud', 'ACTIVE', 3),
  ('ai', '人工智能', 'AI', 'ACTIVE', 4),
  ('career', '职业发展', 'Career', 'ACTIVE', 5);

INSERT INTO tags (name, locale_key, status) VALUES
  ('java', 'tag.java', 'ACTIVE'),
  ('spring-boot', 'tag.spring_boot', 'ACTIVE'),
  ('react', 'tag.react', 'ACTIVE'),
  ('typescript', 'tag.typescript', 'ACTIVE'),
  ('redis', 'tag.redis', 'ACTIVE'),
  ('docker', 'tag.docker', 'ACTIVE');
