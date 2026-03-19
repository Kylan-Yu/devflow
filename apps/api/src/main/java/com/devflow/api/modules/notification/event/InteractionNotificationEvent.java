package com.devflow.api.modules.notification.event;

import java.io.Serializable;
import java.time.LocalDateTime;

public record InteractionNotificationEvent(
        String eventId,
        InteractionEventType eventType,
        Long actorId,
        Long receiverId,
        NotificationTargetType targetType,
        Long targetId,
        String messageCode,
        String preview,
        LocalDateTime occurredAt
) implements Serializable {
}
