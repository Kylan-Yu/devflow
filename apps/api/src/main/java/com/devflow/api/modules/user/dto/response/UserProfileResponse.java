package com.devflow.api.modules.user.dto.response;

import com.devflow.api.modules.user.entity.LanguagePreference;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserRole;
import com.devflow.api.modules.user.entity.UserStatus;
import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        String username,
        String email,
        String displayName,
        String bio,
        LanguagePreference preferredLanguage,
        UserRole role,
        UserStatus status,
        LocalDateTime createdAt
) {
    public static UserProfileResponse from(UserEntity user) {
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getBio(),
                user.getPreferredLanguage(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
