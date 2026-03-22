package com.devflow.api.modules.admin.dto.response;

public record AdminDashboardOverviewResponse(
        long totalUsers,
        long activeUsers,
        long disabledUsers,
        long publishedPosts,
        long hiddenPosts,
        long pendingReports,
        long resolvedReports
) {
}
