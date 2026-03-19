package com.devflow.api.modules.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AdminLoginRequest(
        @NotBlank String username,
        @NotBlank String password
) {
}
