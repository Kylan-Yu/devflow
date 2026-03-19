package com.devflow.api.modules.post.dto.request;

import com.devflow.api.modules.post.entity.PostContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreatePostRequest(
        @NotBlank
        @Size(min = 5, max = 160)
        String title,

        @NotBlank
        @Size(min = 10, max = 12000)
        String content,

        PostContentType contentType,

        @Size(max = 255)
        String coverImageUrl,

        @NotNull
        Long categoryId,

        @Size(max = 10)
        List<Long> tagIds
) {
}
