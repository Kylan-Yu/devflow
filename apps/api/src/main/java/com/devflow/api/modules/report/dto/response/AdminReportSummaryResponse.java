package com.devflow.api.modules.report.dto.response;

import com.devflow.api.modules.report.entity.ReportReason;
import com.devflow.api.modules.report.entity.ReportResolutionAction;
import com.devflow.api.modules.report.entity.ReportStatus;
import com.devflow.api.modules.report.entity.ReportTargetType;
import java.time.LocalDateTime;

public record AdminReportSummaryResponse(
        Long id,
        Long reporterId,
        String reporterUsername,
        String reporterDisplayName,
        ReportTargetType targetType,
        Long targetId,
        String targetLabel,
        String targetStatus,
        ReportReason reason,
        String detail,
        ReportStatus status,
        ReportResolutionAction resolutionAction,
        String resolutionNote,
        String reviewedByAdminDisplayName,
        LocalDateTime createdAt,
        LocalDateTime reviewedAt,
        LocalDateTime updatedAt
) {
}
