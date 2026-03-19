package com.devflow.api.modules.interaction.dto.response;

public record PostInteractionStateResponse(
        boolean liked,
        boolean favorited
) {
}
