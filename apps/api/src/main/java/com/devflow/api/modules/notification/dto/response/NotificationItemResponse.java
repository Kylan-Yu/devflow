package com.devflow.api.modules.notification.dto.response;

import com.devflow.api.modules.notification.entity.NotificationEntity;
import com.devflow.api.modules.notification.entity.NotificationTargetType;
import com.devflow.api.modules.notification.entity.NotificationType;
import java.time.LocalDateTime;

public record NotificationItemResponse(
        Long id,
        Long receiverId,
        Long actorId,
        String actorDisplayName,
        NotificationType type,
        NotificationTargetType targetType,
        Long targetId,
        String messageCode,
        String preview,
        boolean read,
        LocalDateTime createdAt
) {
    public static NotificationItemResponse from(NotificationEntity entity,
                                                String actorDisplayName,
                                                String messageCode,
                                                String preview) {
        return new NotificationItemResponse(
                entity.getId(),
                entity.getReceiverId(),
                entity.getActorId(),
                actorDisplayName,
                entity.getType(),
                entity.getTargetType(),
                entity.getTargetId(),
                messageCode,
                preview,
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}
