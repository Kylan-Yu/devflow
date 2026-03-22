package com.devflow.api.modules.admin.dto.request;

import com.devflow.api.modules.post.entity.PostStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAdminPostStatusRequest(
        @NotNull(message = "status.required")
        PostStatus status
) {
}
