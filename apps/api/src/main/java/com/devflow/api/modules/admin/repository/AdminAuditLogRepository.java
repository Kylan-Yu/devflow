package com.devflow.api.modules.admin.repository;

import com.devflow.api.modules.admin.entity.AdminAuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLogEntity, Long> {

    Page<AdminAuditLogEntity> findAll(Pageable pageable);

    Page<AdminAuditLogEntity> findByActionTypeContainingIgnoreCaseOrTargetTypeContainingIgnoreCaseOrTargetLabelContainingIgnoreCase(String actionType, String targetType, String targetLabel, Pageable pageable);
}
