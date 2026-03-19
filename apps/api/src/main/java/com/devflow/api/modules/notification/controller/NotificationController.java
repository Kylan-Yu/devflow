package com.devflow.api.modules.notification.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.UserPrincipalExtractor;
import com.devflow.api.modules.notification.dto.response.MarkAllReadResponse;
import com.devflow.api.modules.notification.dto.response.NotificationItemResponse;
import com.devflow.api.modules.notification.dto.response.UnreadCountResponse;
import com.devflow.api.modules.notification.service.NotificationService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserPrincipalExtractor userPrincipalExtractor;

    public NotificationController(NotificationService notificationService,
                                  UserPrincipalExtractor userPrincipalExtractor) {
        this.notificationService = notificationService;
        this.userPrincipalExtractor = userPrincipalExtractor;
    }

    @GetMapping
    public ApiResponse<List<NotificationItemResponse>> list(@RequestParam(defaultValue = "20") int size,
                                                            Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(notificationService.listNotifications(userId, size));
    }

    @GetMapping("/unread-count")
    public ApiResponse<UnreadCountResponse> unreadCount(Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(notificationService.unreadCount(userId));
    }

    @PatchMapping("/{id}/read")
    public ApiResponse<Void> markRead(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        notificationService.markAsRead(userId, id);
        return ApiResponse.success();
    }

    @PatchMapping("/read-all")
    public ApiResponse<MarkAllReadResponse> markAllRead(Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        int updatedCount = notificationService.markAllRead(userId);
        return ApiResponse.success(new MarkAllReadResponse(updatedCount));
    }
}
