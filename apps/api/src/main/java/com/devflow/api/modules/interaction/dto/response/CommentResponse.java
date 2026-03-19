package com.devflow.api.modules.interaction.dto.response;

import com.devflow.api.modules.interaction.entity.CommentEntity;
import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long postId,
        Long userId,
        String userDisplayName,
        String content,
        LocalDateTime createdAt
) {
    public static CommentResponse from(CommentEntity entity, String userDisplayName) {
        return new CommentResponse(
                entity.getId(),
                entity.getPostId(),
                entity.getUserId(),
                userDisplayName,
                entity.getContent(),
                entity.getCreatedAt()
        );
    }
}
