package com.devflow.api.modules.auth.dto.request;

import com.devflow.api.modules.user.entity.LanguagePreference;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 32)
        @Pattern(regexp = "^[a-zA-Z0-9_]+$")
        String username,

        @NotBlank
        @Email
        @Size(max = 128)
        String email,

        @NotBlank
        @Size(min = 8, max = 72)
        String password,

        @NotBlank
        @Size(min = 2, max = 64)
        String displayName,

        @NotNull
        LanguagePreference preferredLanguage
) {
}
