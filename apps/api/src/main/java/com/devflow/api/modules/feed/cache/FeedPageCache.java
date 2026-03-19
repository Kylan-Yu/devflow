package com.devflow.api.modules.feed.cache;

import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import java.time.Duration;
import java.util.Optional;

public interface FeedPageCache {

    Optional<CursorPageResponse<PostSummaryResponse>> get(String cacheKey);

    void put(String cacheKey, CursorPageResponse<PostSummaryResponse> value, Duration ttl);

    void evictAll();
}
