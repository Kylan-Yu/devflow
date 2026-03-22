CREATE TABLE reports (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reporter_id BIGINT NOT NULL,
  target_type VARCHAR(16) NOT NULL,
  target_id BIGINT NOT NULL,
  reason VARCHAR(32) NOT NULL,
  detail VARCHAR(500) NULL,
  status VARCHAR(16) NOT NULL,
  resolution_action VARCHAR(32) NOT NULL,
  resolution_note VARCHAR(255) NULL,
  reviewed_by_admin_id BIGINT NULL,
  reviewed_at DATETIME(3) NULL,
  created_at DATETIME(3) NOT NULL,
  updated_at DATETIME(3) NOT NULL
);

CREATE INDEX idx_reports_status_updated ON reports (status, updated_at DESC, id DESC);
CREATE INDEX idx_reports_reporter_created ON reports (reporter_id, created_at DESC, id DESC);
CREATE INDEX idx_reports_target ON reports (target_type, target_id, created_at DESC);
