package com.devflow.api.common.pagination;

import java.time.LocalDateTime;

public record HotCursor(
        Double score,
        LocalDateTime publishedAt,
        Long id
) {
}
