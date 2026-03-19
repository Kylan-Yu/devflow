package com.devflow.api.modules.auth.dto.response;

import com.devflow.api.modules.user.dto.response.UserProfileResponse;

public record AuthSessionResponse(
        AuthTokenResponse tokens,
        UserProfileResponse user
) {
}
