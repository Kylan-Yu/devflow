package com.devflow.api.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank
        String refreshToken
) {
}
