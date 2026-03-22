package com.devflow.api.modules.admin.dto.response;

import com.devflow.api.modules.admin.entity.AdminAuditActionType;
import com.devflow.api.modules.admin.entity.AdminAuditTargetType;
import java.time.LocalDateTime;

public record AdminAuditLogResponse(
        Long id,
        String adminUsername,
        String adminDisplayName,
        AdminAuditActionType actionType,
        AdminAuditTargetType targetType,
        Long targetId,
        String targetLabel,
        String previousState,
        String nextState,
        String resolutionAction,
        String contextLabel,
        LocalDateTime createdAt
) {
}
