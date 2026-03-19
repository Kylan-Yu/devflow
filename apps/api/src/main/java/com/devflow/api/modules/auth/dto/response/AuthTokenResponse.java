package com.devflow.api.modules.auth.dto.response;

import java.time.Instant;

public record AuthTokenResponse(
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
}
