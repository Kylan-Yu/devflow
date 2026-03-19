package com.devflow.api.modules.notification.websocket;

import com.devflow.api.modules.notification.dto.response.NotificationPushMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class NotificationPushGateway {

    private final NotificationWebSocketSessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;

    public NotificationPushGateway(NotificationWebSocketSessionRegistry sessionRegistry, ObjectMapper objectMapper) {
        this.sessionRegistry = sessionRegistry;
        this.objectMapper = objectMapper;
    }

    public void push(Long receiverId, NotificationPushMessage message) {
        String payload = toJson(message);
        for (WebSocketSession session : sessionRegistry.sessions(receiverId)) {
            if (!session.isOpen()) {
                continue;
            }
            try {
                session.sendMessage(new TextMessage(payload));
            } catch (IOException ignored) {
                // EN: Skip disconnected session; it will be removed on close callback.
                // 中文：忽略已断开的会话，后续由关闭回调清理。
            }
        }
    }

    private String toJson(NotificationPushMessage message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize websocket push payload", exception);
        }
    }
}
