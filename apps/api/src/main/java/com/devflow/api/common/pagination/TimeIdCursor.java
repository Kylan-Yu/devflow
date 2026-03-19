package com.devflow.api.common.pagination;

import java.time.LocalDateTime;

public record TimeIdCursor(
        LocalDateTime publishedAt,
        Long id
) {
}
