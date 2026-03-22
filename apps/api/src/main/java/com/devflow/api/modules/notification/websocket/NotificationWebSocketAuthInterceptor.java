package com.devflow.api.modules.notification.websocket;

import com.devflow.api.common.security.PrincipalType;
import com.devflow.api.modules.auth.service.JwtService;
import io.jsonwebtoken.JwtException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class NotificationWebSocketAuthInterceptor implements HandshakeInterceptor {

    public static final String ATTR_USER_ID = "notificationUserId";

    private final JwtService jwtService;

    public NotificationWebSocketAuthInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        String token = resolveAccessToken(request);
        if (token == null || token.isBlank()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        try {
            JwtService.JwtPayload payload = jwtService.parseAccessToken(token);
            if (payload.principalType() != PrincipalType.USER) {
                response.setStatusCode(HttpStatus.FORBIDDEN);
                return false;
            }

            attributes.put(ATTR_USER_ID, payload.subjectId());
            return true;
        } catch (JwtException exception) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // No-op.
    }

    private String resolveAccessToken(ServerHttpRequest request) {
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams();
        List<String> values = queryParams.get("token");
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(0);
    }
}
