package com.devflow.api.modules.auth.dto.response;

import java.time.Instant;

public record AdminLoginResponse(
        String accessToken,
        Instant accessTokenExpiresAt,
        Long adminId,
        String username,
        String displayName
) {
}
