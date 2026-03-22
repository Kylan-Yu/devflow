package com.devflow.api.modules.admin.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.AdminPrincipalExtractor;
import com.devflow.api.modules.admin.dto.request.UpdateAdminPostStatusRequest;
import com.devflow.api.modules.admin.dto.request.UpdateAdminUserStatusRequest;
import com.devflow.api.modules.admin.dto.response.AdminAuditLogResponse;
import com.devflow.api.modules.admin.dto.response.AdminDashboardOverviewResponse;
import com.devflow.api.modules.admin.dto.response.AdminPageResponse;
import com.devflow.api.modules.admin.dto.response.AdminPostSummaryResponse;
import com.devflow.api.modules.admin.dto.response.AdminUserSummaryResponse;
import com.devflow.api.modules.admin.service.AdminModerationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminModerationController {

    private final AdminModerationService adminModerationService;
    private final AdminPrincipalExtractor adminPrincipalExtractor;

    public AdminModerationController(AdminModerationService adminModerationService,
                                     AdminPrincipalExtractor adminPrincipalExtractor) {
        this.adminModerationService = adminModerationService;
        this.adminPrincipalExtractor = adminPrincipalExtractor;
    }

    @GetMapping("/overview")
    public ApiResponse<AdminDashboardOverviewResponse> getOverview(Authentication authentication) {
        adminPrincipalExtractor.requireAdminId(authentication);
        return ApiResponse.success(adminModerationService.getOverview());
    }

    @GetMapping("/users")
    public ApiResponse<AdminPageResponse<AdminUserSummaryResponse>> listUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String search,
            Authentication authentication) {
        adminPrincipalExtractor.requireAdminId(authentication);
        return ApiResponse.success(adminModerationService.listUsers(page, size, search));
    }

    @PatchMapping("/users/{id}/status")
    public ApiResponse<AdminUserSummaryResponse> updateUserStatus(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdateAdminUserStatusRequest request,
                                                                  Authentication authentication) {
        Long adminId = adminPrincipalExtractor.requireAdminId(authentication);
        return ApiResponse.success(adminModerationService.updateUserStatus(adminId, id, request.status()));
    }

    @GetMapping("/posts")
    public ApiResponse<AdminPageResponse<AdminPostSummaryResponse>> listPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String search,
            Authentication authentication) {
        adminPrincipalExtractor.requireAdminId(authentication);
        return ApiResponse.success(adminModerationService.listPosts(page, size, search));
    }

    @PatchMapping("/posts/{id}/status")
    public ApiResponse<AdminPostSummaryResponse> updatePostStatus(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdateAdminPostStatusRequest request,
                                                                  Authentication authentication) {
        Long adminId = adminPrincipalExtractor.requireAdminId(authentication);
        return ApiResponse.success(adminModerationService.updatePostStatus(adminId, id, request.status()));
    }

    @GetMapping("/audit-logs")
    public ApiResponse<AdminPageResponse<AdminAuditLogResponse>> listAuditLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String search,
            Authentication authentication) {
        adminPrincipalExtractor.requireAdminId(authentication);
        return ApiResponse.success(adminModerationService.listAuditLogs(page, size, search));
    }
}
