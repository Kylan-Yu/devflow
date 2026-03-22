package com.devflow.api.modules.admin.dto.request;

import com.devflow.api.modules.user.entity.UserStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateAdminUserStatusRequest(
        @NotNull(message = "status.required")
        UserStatus status
) {
}
