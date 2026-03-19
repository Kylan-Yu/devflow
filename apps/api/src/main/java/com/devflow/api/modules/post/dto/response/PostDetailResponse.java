package com.devflow.api.modules.post.dto.response;

import com.devflow.api.modules.post.entity.PostContentType;
import com.devflow.api.modules.post.entity.PostVisibility;
import java.time.LocalDateTime;
import java.util.List;

public record PostDetailResponse(
        Long id,
        String title,
        String content,
        PostContentType contentType,
        String coverImageUrl,
        PostAuthorResponse author,
        CategoryResponse category,
        List<TagResponse> tags,
        PostVisibility visibility,
        Integer likeCount,
        Integer commentCount,
        Integer favoriteCount,
        Double hotScore,
        LocalDateTime publishedAt,
        LocalDateTime updatedAt
) {
}
