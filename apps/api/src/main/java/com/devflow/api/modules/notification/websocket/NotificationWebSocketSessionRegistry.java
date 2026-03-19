package com.devflow.api.modules.notification.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class NotificationWebSocketSessionRegistry {

    private final ConcurrentMap<Long, Set<WebSocketSession>> sessionsByUser = new ConcurrentHashMap<>();

    public void register(Long userId, WebSocketSession session) {
        sessionsByUser.computeIfAbsent(userId, ignored -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void unregister(Long userId, WebSocketSession session) {
        Set<WebSocketSession> sessions = sessionsByUser.get(userId);
        if (sessions == null) {
            return;
        }
        sessions.remove(session);
        if (sessions.isEmpty()) {
            sessionsByUser.remove(userId);
        }
    }

    public Set<WebSocketSession> sessions(Long userId) {
        return sessionsByUser.getOrDefault(userId, Set.of());
    }
}
