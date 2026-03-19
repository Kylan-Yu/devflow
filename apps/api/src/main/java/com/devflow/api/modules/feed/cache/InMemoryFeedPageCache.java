package com.devflow.api.modules.feed.cache;

import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnMissingBean(FeedPageCache.class)
public class InMemoryFeedPageCache implements FeedPageCache {

    private final ConcurrentMap<String, CacheEntry> store = new ConcurrentHashMap<>();

    @Override
    public Optional<CursorPageResponse<PostSummaryResponse>> get(String cacheKey) {
        CacheEntry cacheEntry = store.get(cacheKey);
        if (cacheEntry == null) {
            return Optional.empty();
        }
        if (cacheEntry.expiresAt().isBefore(Instant.now())) {
            store.remove(cacheKey);
            return Optional.empty();
        }
        return Optional.of(cacheEntry.value());
    }

    @Override
    public void put(String cacheKey, CursorPageResponse<PostSummaryResponse> value, Duration ttl) {
        store.put(cacheKey, new CacheEntry(value, Instant.now().plus(ttl)));
    }

    @Override
    public void evictAll() {
        store.clear();
    }

    private record CacheEntry(CursorPageResponse<PostSummaryResponse> value, Instant expiresAt) {
    }
}
