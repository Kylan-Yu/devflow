package com.devflow.api.modules.feed.cache;

import com.devflow.api.common.cache.RedisCacheClient;
import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "devflow.cache.redis.enabled", havingValue = "true")
public class RedisFeedPageCache implements FeedPageCache {

    private static final String FEED_CACHE_WILDCARD = "devflow:cache:feed:*";

    private final RedisCacheClient redisCacheClient;

    public RedisFeedPageCache(RedisCacheClient redisCacheClient) {
        this.redisCacheClient = redisCacheClient;
    }

    @Override
    public Optional<CursorPageResponse<PostSummaryResponse>> get(String cacheKey) {
        return redisCacheClient.get(cacheKey, new TypeReference<CursorPageResponse<PostSummaryResponse>>() {
        });
    }

    @Override
    public void put(String cacheKey, CursorPageResponse<PostSummaryResponse> value, Duration ttl) {
        redisCacheClient.set(cacheKey, value, ttl);
    }

    @Override
    public void evictAll() {
        redisCacheClient.evictPattern(FEED_CACHE_WILDCARD);
    }
}
