package com.devflow.api.common.pagination;

import java.util.List;

public record CursorPageResponse<T>(
        List<T> items,
        String nextCursor,
        boolean hasMore
) {
}
