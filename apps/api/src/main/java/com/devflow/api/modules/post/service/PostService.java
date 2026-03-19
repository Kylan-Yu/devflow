package com.devflow.api.modules.post.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.cache.CacheKeyBuilder;
import com.devflow.api.common.cache.RedisCacheClient;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.common.pagination.CursorCodec;
import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.common.pagination.TimeIdCursor;
import com.devflow.api.modules.feed.cache.FeedPageCache;
import com.devflow.api.modules.post.dto.request.CreatePostRequest;
import com.devflow.api.modules.post.dto.request.UpdatePostRequest;
import com.devflow.api.modules.post.dto.response.PostDetailResponse;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import com.devflow.api.modules.post.entity.CategoryStatus;
import com.devflow.api.modules.post.entity.PostContentType;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.entity.PostStatus;
import com.devflow.api.modules.post.entity.PostTagEntity;
import com.devflow.api.modules.post.entity.PostVisibility;
import com.devflow.api.modules.post.entity.TagEntity;
import com.devflow.api.modules.post.entity.TagStatus;
import com.devflow.api.modules.post.repository.CategoryRepository;
import com.devflow.api.modules.post.repository.PostQueryRepository;
import com.devflow.api.modules.post.repository.PostRepository;
import com.devflow.api.modules.post.repository.PostTagRepository;
import com.devflow.api.modules.post.repository.TagRepository;
import com.devflow.api.modules.user.entity.UserStatus;
import com.devflow.api.modules.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {

    private static final Duration POST_DETAIL_CACHE_TTL = Duration.ofMinutes(5);

    private final PostRepository postRepository;
    private final PostTagRepository postTagRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final PostQueryRepository postQueryRepository;
    private final PostViewService postViewService;
    private final PostScoreCalculator postScoreCalculator;
    private final CursorCodec cursorCodec;
    private final FeedPageCache feedPageCache;
    private final RedisCacheClient redisCacheClient;

    public PostService(PostRepository postRepository,
                       PostTagRepository postTagRepository,
                       CategoryRepository categoryRepository,
                       TagRepository tagRepository,
                       UserRepository userRepository,
                       PostQueryRepository postQueryRepository,
                       PostViewService postViewService,
                       PostScoreCalculator postScoreCalculator,
                       CursorCodec cursorCodec,
                       FeedPageCache feedPageCache,
                       RedisCacheClient redisCacheClient) {
        this.postRepository = postRepository;
        this.postTagRepository = postTagRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.postQueryRepository = postQueryRepository;
        this.postViewService = postViewService;
        this.postScoreCalculator = postScoreCalculator;
        this.cursorCodec = cursorCodec;
        this.feedPageCache = feedPageCache;
        this.redisCacheClient = redisCacheClient;
    }

    @Transactional
    public PostDetailResponse createPost(Long userId, CreatePostRequest request) {
        ensureUserExists(userId);
        validateCategory(request.categoryId());
        List<Long> tagIds = validateAndNormalizeTags(request.tagIds());

        LocalDateTime now = LocalDateTime.now();
        PostEntity post = new PostEntity();
        post.setAuthorId(userId);
        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        post.setContentType(request.contentType() == null ? PostContentType.MARKDOWN : request.contentType());
        post.setCoverImageUrl(trimNullable(request.coverImageUrl()));
        post.setCategoryId(request.categoryId());
        post.setStatus(PostStatus.PUBLISHED);
        post.setVisibility(PostVisibility.PUBLIC);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setFavoriteCount(0);
        post.setPublishedAt(now);
        post.setScore(postScoreCalculator.calculateHotScore(0, 0, 0, now));
        post.setCreatedAt(now);
        post.setUpdatedAt(now);

        PostEntity savedPost = postRepository.save(post);
        replacePostTags(savedPost.getId(), tagIds);
        feedPageCache.evictAll();
        evictPostDetailCache(savedPost.getId());
        return postViewService.toDetail(savedPost);
    }

    @Transactional
    public PostDetailResponse updatePost(Long userId, Long postId, UpdatePostRequest request) {
        validateCategory(request.categoryId());
        List<Long> tagIds = validateAndNormalizeTags(request.tagIds());

        PostEntity post = loadPublishedPost(postId);
        if (!post.getAuthorId().equals(userId)) {
            throw new BusinessException(ResponseCode.POST_EDIT_FORBIDDEN);
        }

        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        post.setContentType(request.contentType() == null ? PostContentType.MARKDOWN : request.contentType());
        post.setCoverImageUrl(trimNullable(request.coverImageUrl()));
        post.setCategoryId(request.categoryId());
        post.setScore(postScoreCalculator.calculateHotScore(
                post.getLikeCount(), post.getCommentCount(), post.getFavoriteCount(), post.getPublishedAt()));
        post.setUpdatedAt(LocalDateTime.now());

        PostEntity savedPost = postRepository.save(post);
        replacePostTags(savedPost.getId(), tagIds);
        feedPageCache.evictAll();
        evictPostDetailCache(savedPost.getId());
        return postViewService.toDetail(savedPost);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        PostEntity post = loadPublishedPost(postId);
        if (!post.getAuthorId().equals(userId)) {
            throw new BusinessException(ResponseCode.POST_EDIT_FORBIDDEN);
        }

        post.setStatus(PostStatus.DELETED);
        post.setDeletedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);
        feedPageCache.evictAll();
        evictPostDetailCache(postId);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        String cacheKey = CacheKeyBuilder.postDetail(postId);
        var cached = redisCacheClient.get(cacheKey, PostDetailResponse.class);
        if (cached.isPresent()) {
            return cached.get();
        }

        PostEntity post = postRepository.findByIdAndStatusAndDeletedAtIsNull(postId, PostStatus.PUBLISHED)
                .filter(item -> item.getVisibility() == PostVisibility.PUBLIC)
                .orElseThrow(() -> new BusinessException(ResponseCode.POST_NOT_FOUND));
        PostDetailResponse detail = postViewService.toDetail(post);
        // EN: Post detail cache focuses on read-heavy detail page traffic.
        redisCacheClient.set(cacheKey, detail, POST_DETAIL_CACHE_TTL);
        return detail;
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<PostSummaryResponse> getPostsByAuthor(Long authorId, String cursor, int size) {
        ensureUserExists(authorId);
        int normalizedSize = normalizePageSize(size);

        TimeIdCursor decodedCursor = cursor == null || cursor.isBlank()
                ? null
                : cursorCodec.decode(cursor, TimeIdCursor.class);

        List<PostEntity> rows = postQueryRepository.findPublishedPostsByAuthor(
                authorId,
                decodedCursor == null ? null : decodedCursor.publishedAt(),
                decodedCursor == null ? null : decodedCursor.id(),
                normalizedSize + 1
        );

        boolean hasMore = rows.size() > normalizedSize;
        List<PostEntity> pageItems = hasMore ? rows.subList(0, normalizedSize) : rows;
        List<PostSummaryResponse> summaries = postViewService.toSummaries(pageItems);
        String nextCursor = null;
        if (hasMore && !summaries.isEmpty()) {
            PostSummaryResponse last = summaries.get(summaries.size() - 1);
            nextCursor = cursorCodec.encode(new TimeIdCursor(last.publishedAt(), last.id()));
        }

        return new CursorPageResponse<>(summaries, nextCursor, hasMore);
    }

    private PostEntity loadPublishedPost(Long postId) {
        return postRepository.findByIdAndStatusAndDeletedAtIsNull(postId, PostStatus.PUBLISHED)
                .orElseThrow(() -> new BusinessException(ResponseCode.POST_NOT_FOUND));
    }

    private void ensureUserExists(Long userId) {
        userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private void validateCategory(Long categoryId) {
        if (!categoryRepository.existsByIdAndStatus(categoryId, CategoryStatus.ACTIVE)) {
            throw new BusinessException(ResponseCode.CATEGORY_NOT_FOUND);
        }
    }

    private List<Long> validateAndNormalizeTags(List<Long> rawTagIds) {
        if (rawTagIds == null || rawTagIds.isEmpty()) {
            return List.of();
        }

        Set<Long> normalized = new LinkedHashSet<>(rawTagIds);
        List<TagEntity> tags = tagRepository.findAllById(normalized);
        if (tags.size() != normalized.size()) {
            throw new BusinessException(ResponseCode.TAG_NOT_FOUND);
        }
        boolean allActive = tags.stream().allMatch(tag -> tag.getStatus() == TagStatus.ACTIVE);
        if (!allActive) {
            throw new BusinessException(ResponseCode.TAG_NOT_FOUND);
        }
        return normalized.stream().toList();
    }

    private void replacePostTags(Long postId, List<Long> tagIds) {
        postTagRepository.deleteByPostId(postId);
        if (tagIds.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        List<PostTagEntity> mappings = tagIds.stream().map(tagId -> {
            PostTagEntity postTag = new PostTagEntity();
            postTag.setPostId(postId);
            postTag.setTagId(tagId);
            postTag.setCreatedAt(now);
            return postTag;
        }).toList();
        postTagRepository.saveAll(mappings);
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

    public void evictPostDetailCache(Long postId) {
        redisCacheClient.evict(CacheKeyBuilder.postDetail(postId));
    }
}
