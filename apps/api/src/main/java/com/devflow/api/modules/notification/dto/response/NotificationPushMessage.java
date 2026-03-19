package com.devflow.api.modules.notification.dto.response;

import com.devflow.api.modules.notification.event.InteractionEventType;
import com.devflow.api.modules.notification.event.NotificationTargetType;
import java.time.LocalDateTime;

public record NotificationPushMessage(
        Long notificationId,
        InteractionEventType eventType,
        Long actorId,
        Long receiverId,
        NotificationTargetType targetType,
        Long targetId,
        String messageCode,
        String preview,
        long unreadCount,
        LocalDateTime createdAt
) {
}
