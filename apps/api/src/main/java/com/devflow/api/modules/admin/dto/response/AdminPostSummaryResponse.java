package com.devflow.api.modules.admin.dto.response;

import com.devflow.api.modules.post.entity.PostStatus;
import java.time.LocalDateTime;

public record AdminPostSummaryResponse(
        Long id,
        String title,
        String authorUsername,
        String authorDisplayName,
        String categoryCode,
        String categoryNameZh,
        String categoryNameEn,
        PostStatus status,
        Integer likeCount,
        Integer commentCount,
        Integer favoriteCount,
        LocalDateTime publishedAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt
) {
}
