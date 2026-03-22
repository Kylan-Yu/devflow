CREATE TABLE admin_audit_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  admin_user_id BIGINT NOT NULL,
  action_type VARCHAR(32) NOT NULL,
  target_type VARCHAR(16) NOT NULL,
  target_id BIGINT NOT NULL,
  target_label VARCHAR(255) NOT NULL,
  previous_state VARCHAR(64) NULL,
  next_state VARCHAR(64) NULL,
  resolution_action VARCHAR(32) NULL,
  context_label VARCHAR(100) NULL,
  created_at DATETIME(3) NOT NULL
);

CREATE INDEX idx_admin_audit_logs_created ON admin_audit_logs (created_at DESC, id DESC);
CREATE INDEX idx_admin_audit_logs_admin_created ON admin_audit_logs (admin_user_id, created_at DESC, id DESC);
