package com.devflow.api.modules.report.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.AdminPrincipalExtractor;
import com.devflow.api.modules.report.dto.request.ReviewReportRequest;
import com.devflow.api.modules.report.dto.response.AdminReportSummaryResponse;
import com.devflow.api.modules.report.entity.ReportStatus;
import com.devflow.api.modules.report.service.ReportService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/reports")
public class AdminReportController {

    private final ReportService reportService;
    private final AdminPrincipalExtractor adminPrincipalExtractor;

    public AdminReportController(ReportService reportService,
                                 AdminPrincipalExtractor adminPrincipalExtractor) {
        this.reportService = reportService;
        this.adminPrincipalExtractor = adminPrincipalExtractor;
    }

    @GetMapping
    public ApiResponse<List<AdminReportSummaryResponse>> listReports(@RequestParam(required = false) ReportStatus status,
                                                                     @RequestParam(defaultValue = "12") Integer size,
                                                                     Authentication authentication) {
        adminPrincipalExtractor.requireAdminId(authentication);
        return ApiResponse.success(reportService.listAdminReports(status, size == null ? 12 : size));
    }

    @PatchMapping("/{id}")
    public ApiResponse<AdminReportSummaryResponse> reviewReport(@PathVariable Long id,
                                                                @Valid @RequestBody ReviewReportRequest request,
                                                                Authentication authentication) {
        Long adminId = adminPrincipalExtractor.requireAdminId(authentication);
        return ApiResponse.success(reportService.reviewReport(adminId, id, request));
    }
}
