package com.devflow.api.modules.notification.websocket;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final NotificationWebSocketSessionRegistry sessionRegistry;
    private final Map<String, Long> userIdBySessionId = new ConcurrentHashMap<>();

    public NotificationWebSocketHandler(NotificationWebSocketSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        resolveUserId(session.getUri()).ifPresent(userId -> {
            userIdBySessionId.put(session.getId(), userId);
            sessionRegistry.register(userId, session);
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = userIdBySessionId.remove(session.getId());
        if (userId != null) {
            sessionRegistry.unregister(userId, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        // EN: Client messages are ignored for now.
        // 中文：当前阶段忽略客户端主动消息。
    }

    private Optional<Long> resolveUserId(URI uri) {
        if (uri == null || uri.getQuery() == null || uri.getQuery().isBlank()) {
            return Optional.empty();
        }

        Map<String, String> queryParams = Arrays.stream(uri.getQuery().split("&"))
                .map(item -> item.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1], (left, right) -> right));

        String raw = queryParams.get("userId");
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(raw));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
