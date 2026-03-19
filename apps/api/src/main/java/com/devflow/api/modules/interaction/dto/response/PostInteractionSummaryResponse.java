package com.devflow.api.modules.interaction.dto.response;

public record PostInteractionSummaryResponse(
        Long postId,
        int likeCount,
        int commentCount,
        int favoriteCount,
        double hotScore
) {
}
