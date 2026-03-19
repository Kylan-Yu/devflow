package com.devflow.api.modules.notification.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.cache.CacheKeyBuilder;
import com.devflow.api.common.cache.RedisCacheClient;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.modules.notification.dto.response.NotificationItemResponse;
import com.devflow.api.modules.notification.dto.response.NotificationPushMessage;
import com.devflow.api.modules.notification.dto.response.UnreadCountResponse;
import com.devflow.api.modules.notification.entity.NotificationEntity;
import com.devflow.api.modules.notification.entity.NotificationTargetType;
import com.devflow.api.modules.notification.entity.NotificationType;
import com.devflow.api.modules.notification.event.InteractionEventType;
import com.devflow.api.modules.notification.event.InteractionNotificationEvent;
import com.devflow.api.modules.notification.repository.NotificationRepository;
import com.devflow.api.modules.notification.websocket.NotificationPushGateway;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private static final Duration UNREAD_COUNT_CACHE_TTL = Duration.ofSeconds(30);

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationPushGateway notificationPushGateway;
    private final ObjectMapper objectMapper;
    private final RedisCacheClient redisCacheClient;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               NotificationPushGateway notificationPushGateway,
                               ObjectMapper objectMapper,
                               RedisCacheClient redisCacheClient) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.notificationPushGateway = notificationPushGateway;
        this.objectMapper = objectMapper;
        this.redisCacheClient = redisCacheClient;
    }

    @Transactional
    public void handleInteractionEvent(InteractionNotificationEvent event) {
        if (event.receiverId() == null || event.actorId() == null || event.targetId() == null) {
            return;
        }
        if (event.receiverId().equals(event.actorId())) {
            return;
        }

        NotificationEntity notification = new NotificationEntity();
        notification.setReceiverId(event.receiverId());
        notification.setActorId(event.actorId());
        notification.setType(mapType(event.eventType()));
        notification.setTargetType(mapTargetType(event.targetType()));
        notification.setTargetId(event.targetId());
        notification.setPayloadJson(toPayloadJson(event.messageCode(), event.preview()));
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        NotificationEntity saved = notificationRepository.save(notification);
        long unreadCount = notificationRepository.countByReceiverIdAndIsReadFalse(event.receiverId());
        redisCacheClient.set(
                CacheKeyBuilder.notificationUnread(event.receiverId()),
                new UnreadCountResponse(unreadCount),
                UNREAD_COUNT_CACHE_TTL
        );

        NotificationPushMessage pushMessage = new NotificationPushMessage(
                saved.getId(),
                event.eventType(),
                event.actorId(),
                event.receiverId(),
                event.targetType(),
                event.targetId(),
                event.messageCode(),
                event.preview(),
                unreadCount,
                saved.getCreatedAt()
        );
        notificationPushGateway.push(event.receiverId(), pushMessage);
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse unreadCount(Long receiverId) {
        String cacheKey = CacheKeyBuilder.notificationUnread(receiverId);
        var cached = redisCacheClient.get(cacheKey, UnreadCountResponse.class);
        if (cached.isPresent()) {
            return cached.get();
        }

        UnreadCountResponse unread = new UnreadCountResponse(
                notificationRepository.countByReceiverIdAndIsReadFalse(receiverId));
        // EN: Unread count uses short TTL for near real-time user experience.
        redisCacheClient.set(cacheKey, unread, UNREAD_COUNT_CACHE_TTL);
        return unread;
    }

    @Transactional(readOnly = true)
    public List<NotificationItemResponse> listNotifications(Long receiverId, int size) {
        int normalized = normalizeSize(size);
        List<NotificationEntity> entities = notificationRepository.findTop100ByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .limit(normalized)
                .toList();

        Set<Long> actorIds = entities.stream().map(NotificationEntity::getActorId).collect(java.util.stream.Collectors.toSet());
        Map<Long, String> actorNameMap = new HashMap<>();
        if (!actorIds.isEmpty()) {
            for (UserEntity user : userRepository.findAllById(actorIds)) {
                actorNameMap.put(user.getId(), user.getDisplayName());
            }
        }

        return entities.stream().map(entity -> {
            ParsedPayload payload = parsePayload(entity.getPayloadJson());
            String actorName = actorNameMap.getOrDefault(entity.getActorId(), "Unknown User");
            return NotificationItemResponse.from(entity, actorName, payload.messageCode(), payload.preview());
        }).toList();
    }

    @Transactional
    public void markAsRead(Long receiverId, Long notificationId) {
        NotificationEntity notification = notificationRepository.findByIdAndReceiverId(notificationId, receiverId)
                .orElseThrow(() -> new BusinessException(ResponseCode.NOTIFICATION_NOT_FOUND));
        if (!notification.isRead()) {
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
            redisCacheClient.evict(CacheKeyBuilder.notificationUnread(receiverId));
        }
    }

    @Transactional
    public int markAllRead(Long receiverId) {
        int updated = notificationRepository.markAllRead(receiverId, LocalDateTime.now());
        redisCacheClient.evict(CacheKeyBuilder.notificationUnread(receiverId));
        return updated;
    }

    private NotificationType mapType(InteractionEventType eventType) {
        return switch (eventType) {
            case POST_LIKED -> NotificationType.LIKE;
            case POST_COMMENTED -> NotificationType.COMMENT;
            case USER_FOLLOWED -> NotificationType.FOLLOW;
        };
    }

    private NotificationTargetType mapTargetType(com.devflow.api.modules.notification.event.NotificationTargetType eventTargetType) {
        return switch (eventTargetType) {
            case POST -> NotificationTargetType.POST;
            case COMMENT -> NotificationTargetType.COMMENT;
            case USER -> NotificationTargetType.USER;
        };
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return 20;
        }
        return Math.min(size, 100);
    }

    private String toPayloadJson(String messageCode, String preview) {
        Map<String, String> payload = Map.of(
                "messageCode", messageCode == null ? "notification.unknown" : messageCode,
                "preview", preview == null ? "" : preview
        );
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new BusinessException(ResponseCode.INTERNAL_ERROR);
        }
    }

    private ParsedPayload parsePayload(String payloadJson) {
        if (payloadJson == null || payloadJson.isBlank()) {
            return new ParsedPayload("notification.unknown", "");
        }
        try {
            Map<String, Object> payload = objectMapper.readValue(payloadJson, new TypeReference<>() {
            });
            Object rawMessageCode = payload.get("messageCode");
            Object rawPreview = payload.get("preview");
            String messageCode = rawMessageCode == null ? "notification.unknown" : String.valueOf(rawMessageCode);
            String preview = rawPreview == null ? "" : String.valueOf(rawPreview);
            return new ParsedPayload(messageCode, preview);
        } catch (JsonProcessingException exception) {
            return new ParsedPayload("notification.unknown", "");
        }
    }

    private record ParsedPayload(String messageCode, String preview) {
    }
}
