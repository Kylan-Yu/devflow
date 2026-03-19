package com.devflow.api.modules.post.dto.response;

import com.devflow.api.modules.user.entity.UserEntity;

public record PostAuthorResponse(
        Long id,
        String username,
        String displayName
) {
    public static PostAuthorResponse from(UserEntity user) {
        return new PostAuthorResponse(user.getId(), user.getUsername(), user.getDisplayName());
    }

    public static PostAuthorResponse fallback(Long userId) {
        return new PostAuthorResponse(userId, "unknown", "Unknown User");
    }
}
