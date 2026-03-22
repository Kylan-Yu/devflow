package com.devflow.api.modules.notification.config;

import com.devflow.api.modules.notification.websocket.NotificationWebSocketAuthInterceptor;
import com.devflow.api.modules.notification.websocket.NotificationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class NotificationWebSocketConfig implements WebSocketConfigurer {

    private final NotificationWebSocketHandler notificationWebSocketHandler;
    private final NotificationWebSocketAuthInterceptor notificationWebSocketAuthInterceptor;

    public NotificationWebSocketConfig(NotificationWebSocketHandler notificationWebSocketHandler,
                                       NotificationWebSocketAuthInterceptor notificationWebSocketAuthInterceptor) {
        this.notificationWebSocketHandler = notificationWebSocketHandler;
        this.notificationWebSocketAuthInterceptor = notificationWebSocketAuthInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(notificationWebSocketHandler, "/ws/notifications")
                .addInterceptors(notificationWebSocketAuthInterceptor)
                .setAllowedOrigins("http://localhost:5173", "http://localhost:5174");
    }
}
