package com.devflow.api.modules.admin.dto.response;

import com.devflow.api.modules.user.entity.LanguagePreference;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserStatus;
import java.time.LocalDateTime;

public record AdminUserSummaryResponse(
        Long id,
        String username,
        String email,
        String displayName,
        LanguagePreference preferredLanguage,
        UserStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime lastLoginAt
) {
    public static AdminUserSummaryResponse from(UserEntity user) {
        return new AdminUserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getDisplayName(),
                user.getPreferredLanguage(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt()
        );
    }
}
