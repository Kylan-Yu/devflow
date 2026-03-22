package com.devflow.api.modules.report.dto.request;

import com.devflow.api.modules.report.entity.ReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReportRequest(
        @NotNull
        ReportReason reason,

        @Size(max = 500)
        String detail
) {
}
