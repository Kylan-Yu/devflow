package com.devflow.api.modules.report.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.UserPrincipalExtractor;
import com.devflow.api.modules.report.dto.request.CreateReportRequest;
import com.devflow.api.modules.report.dto.response.ReportItemResponse;
import com.devflow.api.modules.report.service.ReportService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ReportController {

    private final ReportService reportService;
    private final UserPrincipalExtractor userPrincipalExtractor;

    public ReportController(ReportService reportService, UserPrincipalExtractor userPrincipalExtractor) {
        this.reportService = reportService;
        this.userPrincipalExtractor = userPrincipalExtractor;
    }

    @PostMapping("/posts/{id}/reports")
    public ResponseEntity<ApiResponse<ReportItemResponse>> reportPost(@PathVariable Long id,
                                                                      @Valid @RequestBody CreateReportRequest request,
                                                                      Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        ReportItemResponse response = reportService.createPostReport(userId, id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PostMapping("/users/{id}/reports")
    public ResponseEntity<ApiResponse<ReportItemResponse>> reportUser(@PathVariable Long id,
                                                                      @Valid @RequestBody CreateReportRequest request,
                                                                      Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        ReportItemResponse response = reportService.createUserReport(userId, id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @GetMapping("/reports/me")
    public ApiResponse<List<ReportItemResponse>> listMyReports(@RequestParam(defaultValue = "20") Integer size,
                                                               Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(reportService.listMyReports(userId, size == null ? 20 : size));
    }
}
