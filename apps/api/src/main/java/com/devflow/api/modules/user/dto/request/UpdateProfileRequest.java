package com.devflow.api.modules.user.dto.request;

import com.devflow.api.modules.user.entity.LanguagePreference;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @Size(min = 2, max = 64) String displayName,
        @Size(max = 255) String bio,
        LanguagePreference preferredLanguage
) {
}
