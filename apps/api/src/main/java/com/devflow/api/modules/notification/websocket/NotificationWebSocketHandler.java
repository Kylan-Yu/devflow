package com.devflow.api.modules.notification.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
        Object rawUserId = session.getAttributes().get(NotificationWebSocketAuthInterceptor.ATTR_USER_ID);
        if (rawUserId instanceof Long userId) {
            userIdBySessionId.put(session.getId(), userId);
            sessionRegistry.register(userId, session);
            return;
        }

        try {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Missing websocket auth context"));
        } catch (Exception ignored) {
            // Ignore close failure for invalid websocket session.
        }
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
        // CN: Client-initiated websocket messages are ignored for now.
    }
}
