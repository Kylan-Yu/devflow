package com.devflow.api.modules.report.dto.request;

import com.devflow.api.modules.report.entity.ReportResolutionAction;
import com.devflow.api.modules.report.entity.ReportStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewReportRequest(
        @NotNull
        ReportStatus status,

        @NotNull
        ReportResolutionAction resolutionAction,

        @Size(max = 255)
        String resolutionNote
) {
}
