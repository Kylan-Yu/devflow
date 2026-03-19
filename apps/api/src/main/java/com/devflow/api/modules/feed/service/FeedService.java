package com.devflow.api.modules.feed.service;

import com.devflow.api.common.pagination.CursorCodec;
import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.common.pagination.HotCursor;
import com.devflow.api.common.pagination.TimeIdCursor;
import com.devflow.api.common.cache.CacheKeyBuilder;
import com.devflow.api.modules.feed.cache.FeedPageCache;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.repository.PostQueryRepository;
import com.devflow.api.modules.post.service.PostViewService;
import java.time.Duration;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FeedService {

    private static final Duration DEFAULT_CACHE_TTL = Duration.ofSeconds(45);

    private final PostQueryRepository postQueryRepository;
    private final PostViewService postViewService;
    private final CursorCodec cursorCodec;
    private final FeedPageCache feedPageCache;

    public FeedService(PostQueryRepository postQueryRepository,
                       PostViewService postViewService,
                       CursorCodec cursorCodec,
                       FeedPageCache feedPageCache) {
        this.postQueryRepository = postQueryRepository;
        this.postViewService = postViewService;
        this.cursorCodec = cursorCodec;
        this.feedPageCache = feedPageCache;
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<PostSummaryResponse> latestFeed(String cursor, int size, Long categoryId) {
        int normalizedSize = normalizePageSize(size);
        boolean isFirstPage = cursor == null || cursor.isBlank();
        // EN: Cache key format keeps feed dimensions explicit for predictable invalidation.
        String cacheKey = CacheKeyBuilder.feedFirstPage("latest", normalizedSize, categoryId);

        if (isFirstPage) {
            var cached = feedPageCache.get(cacheKey);
            if (cached.isPresent()) {
                return cached.get();
            }
        }

        TimeIdCursor decodedCursor = isFirstPage ? null : cursorCodec.decode(cursor, TimeIdCursor.class);
        List<PostEntity> rows = postQueryRepository.findLatestPublishedPosts(
                decodedCursor == null ? null : decodedCursor.publishedAt(),
                decodedCursor == null ? null : decodedCursor.id(),
                categoryId,
                normalizedSize + 1
        );

        CursorPageResponse<PostSummaryResponse> response = toTimeCursorPage(rows, normalizedSize);
        if (isFirstPage) {
            feedPageCache.put(cacheKey, response, DEFAULT_CACHE_TTL);
        }
        return response;
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<PostSummaryResponse> hotFeed(String cursor, int size, Long categoryId) {
        int normalizedSize = normalizePageSize(size);
        boolean isFirstPage = cursor == null || cursor.isBlank();
        String cacheKey = CacheKeyBuilder.feedFirstPage("hot", normalizedSize, categoryId);

        if (isFirstPage) {
            var cached = feedPageCache.get(cacheKey);
            if (cached.isPresent()) {
                return cached.get();
            }
        }

        HotCursor decodedCursor = isFirstPage ? null : cursorCodec.decode(cursor, HotCursor.class);
        List<PostEntity> rows = postQueryRepository.findHotPublishedPosts(
                decodedCursor == null ? null : decodedCursor.score(),
                decodedCursor == null ? null : decodedCursor.publishedAt(),
                decodedCursor == null ? null : decodedCursor.id(),
                categoryId,
                normalizedSize + 1
        );

        CursorPageResponse<PostSummaryResponse> response = toHotCursorPage(rows, normalizedSize);
        if (isFirstPage) {
            feedPageCache.put(cacheKey, response, DEFAULT_CACHE_TTL);
        }
        return response;
    }

    private CursorPageResponse<PostSummaryResponse> toTimeCursorPage(List<PostEntity> rows, int pageSize) {
        boolean hasMore = rows.size() > pageSize;
        List<PostEntity> pageItems = hasMore ? rows.subList(0, pageSize) : rows;
        List<PostSummaryResponse> items = postViewService.toSummaries(pageItems);
        String nextCursor = null;
        if (hasMore && !items.isEmpty()) {
            PostSummaryResponse last = items.get(items.size() - 1);
            nextCursor = cursorCodec.encode(new TimeIdCursor(last.publishedAt(), last.id()));
        }
        return new CursorPageResponse<>(items, nextCursor, hasMore);
    }

    private CursorPageResponse<PostSummaryResponse> toHotCursorPage(List<PostEntity> rows, int pageSize) {
        boolean hasMore = rows.size() > pageSize;
        List<PostEntity> pageItems = hasMore ? rows.subList(0, pageSize) : rows;
        List<PostSummaryResponse> items = postViewService.toSummaries(pageItems);
        String nextCursor = null;
        if (hasMore && !items.isEmpty()) {
            PostSummaryResponse last = items.get(items.size() - 1);
            nextCursor = cursorCodec.encode(new HotCursor(last.hotScore(), last.publishedAt(), last.id()));
        }
        return new CursorPageResponse<>(items, nextCursor, hasMore);
    }

    private int normalizePageSize(int size) {
        if (size <= 0) {
            return 10;
        }
        return Math.min(size, 30);
    }
}
