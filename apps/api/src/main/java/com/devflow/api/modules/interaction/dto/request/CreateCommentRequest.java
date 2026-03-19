package com.devflow.api.modules.interaction.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotBlank
        @Size(max = 2000)
        String content
) {
}
