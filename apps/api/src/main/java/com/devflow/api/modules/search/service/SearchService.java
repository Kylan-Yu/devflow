package com.devflow.api.modules.search.service;

import com.devflow.api.common.pagination.CursorCodec;
import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.common.pagination.TimeIdCursor;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.repository.PostQueryRepository;
import com.devflow.api.modules.post.service.PostViewService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchService {

    private final PostQueryRepository postQueryRepository;
    private final PostViewService postViewService;
    private final CursorCodec cursorCodec;

    public SearchService(PostQueryRepository postQueryRepository,
                         PostViewService postViewService,
                         CursorCodec cursorCodec) {
        this.postQueryRepository = postQueryRepository;
        this.postViewService = postViewService;
        this.cursorCodec = cursorCodec;
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<PostSummaryResponse> searchPosts(String keyword,
                                                               Long categoryId,
                                                               String cursor,
                                                               int size) {
        String normalizedKeyword = trimNullable(keyword);
        if (normalizedKeyword == null && categoryId == null) {
            return new CursorPageResponse<>(List.of(), null, false);
        }

        int normalizedSize = normalizePageSize(size);
        TimeIdCursor decodedCursor = cursor == null || cursor.isBlank()
                ? null
                : cursorCodec.decode(cursor, TimeIdCursor.class);

        List<PostEntity> rows = postQueryRepository.searchPublishedPosts(
                normalizedKeyword,
                categoryId,
                decodedCursor == null ? null : decodedCursor.publishedAt(),
                decodedCursor == null ? null : decodedCursor.id(),
                normalizedSize + 1
        );

        boolean hasMore = rows.size() > normalizedSize;
        List<PostEntity> pageItems = hasMore ? rows.subList(0, normalizedSize) : rows;
        List<PostSummaryResponse> items = postViewService.toSummaries(pageItems);
        String nextCursor = null;
        if (hasMore && !items.isEmpty()) {
            PostSummaryResponse last = items.get(items.size() - 1);
            nextCursor = cursorCodec.encode(new TimeIdCursor(last.publishedAt(), last.id()));
        }

        return new CursorPageResponse<>(items, nextCursor, hasMore);
    }

    private int normalizePageSize(int size) {
        if (size <= 0) {
            return 10;
        }
        return Math.min(size, 30);
    }

    private String trimNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
