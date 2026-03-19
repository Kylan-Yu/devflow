CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(32) NOT NULL,
  email VARCHAR(128) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  display_name VARCHAR(64) NOT NULL,
  bio VARCHAR(255) NULL,
  preferred_language VARCHAR(16) NOT NULL,
  role VARCHAR(16) NOT NULL,
  status VARCHAR(16) NOT NULL,
  last_login_at DATETIME(3) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT uq_users_username UNIQUE (username),
  CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE refresh_tokens (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  principal_type VARCHAR(16) NOT NULL,
  subject_id BIGINT NOT NULL,
  token_hash CHAR(64) NOT NULL,
  expires_at DATETIME(3) NOT NULL,
  revoked TINYINT(1) NOT NULL DEFAULT 0,
  revoked_at DATETIME(3) NULL,
  replaced_by_hash CHAR(64) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT uq_refresh_tokens_hash UNIQUE (token_hash),
  INDEX idx_refresh_tokens_subject (principal_type, subject_id),
  INDEX idx_refresh_tokens_expires_at (expires_at)
);

CREATE TABLE admin_users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  display_name VARCHAR(64) NOT NULL,
  status VARCHAR(16) NOT NULL,
  last_login_at DATETIME(3) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  updated_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  CONSTRAINT uq_admin_users_username UNIQUE (username)
);
