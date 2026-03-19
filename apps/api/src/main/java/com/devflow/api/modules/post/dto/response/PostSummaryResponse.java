package com.devflow.api.modules.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record PostSummaryResponse(
        Long id,
        String title,
        String excerpt,
        String coverImageUrl,
        PostAuthorResponse author,
        CategoryResponse category,
        List<TagResponse> tags,
        Integer likeCount,
        Integer commentCount,
        Integer favoriteCount,
        Double hotScore,
        LocalDateTime publishedAt
) {
}
