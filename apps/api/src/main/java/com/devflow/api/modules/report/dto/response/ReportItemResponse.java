package com.devflow.api.modules.report.dto.response;

import com.devflow.api.modules.report.entity.ReportReason;
import com.devflow.api.modules.report.entity.ReportResolutionAction;
import com.devflow.api.modules.report.entity.ReportStatus;
import com.devflow.api.modules.report.entity.ReportTargetType;
import java.time.LocalDateTime;

public record ReportItemResponse(
        Long id,
        ReportTargetType targetType,
        Long targetId,
        String targetLabel,
        String targetStatus,
        ReportReason reason,
        String detail,
        ReportStatus status,
        ReportResolutionAction resolutionAction,
        String resolutionNote,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt
) {
}
