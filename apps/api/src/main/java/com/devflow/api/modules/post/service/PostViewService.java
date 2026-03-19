package com.devflow.api.modules.post.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.modules.post.dto.response.CategoryResponse;
import com.devflow.api.modules.post.dto.response.PostAuthorResponse;
import com.devflow.api.modules.post.dto.response.PostDetailResponse;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import com.devflow.api.modules.post.dto.response.TagResponse;
import com.devflow.api.modules.post.entity.CategoryEntity;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.entity.PostTagEntity;
import com.devflow.api.modules.post.entity.TagEntity;
import com.devflow.api.modules.post.repository.CategoryRepository;
import com.devflow.api.modules.post.repository.PostTagRepository;
import com.devflow.api.modules.post.repository.TagRepository;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostViewService {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final UserRepository userRepository;
    private final PostScoreCalculator postScoreCalculator;

    public PostViewService(CategoryRepository categoryRepository,
                           TagRepository tagRepository,
                           PostTagRepository postTagRepository,
                           UserRepository userRepository,
                           PostScoreCalculator postScoreCalculator) {
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.postTagRepository = postTagRepository;
        this.userRepository = userRepository;
        this.postScoreCalculator = postScoreCalculator;
    }

    @Transactional(readOnly = true)
    public PostDetailResponse toDetail(PostEntity post) {
        Map<Long, CategoryEntity> categoryMap = loadCategoryMap(List.of(post.getCategoryId()));
        Map<Long, List<TagEntity>> tagMap = loadTagMap(List.of(post.getId()));
        Map<Long, UserEntity> authorMap = loadAuthorMap(List.of(post.getAuthorId()));

        return buildDetail(post, categoryMap, tagMap, authorMap);
    }

    @Transactional(readOnly = true)
    public List<PostSummaryResponse> toSummaries(List<PostEntity> posts) {
        if (posts.isEmpty()) {
            return List.of();
        }

        Map<Long, CategoryEntity> categoryMap = loadCategoryMap(
                posts.stream().map(PostEntity::getCategoryId).collect(Collectors.toSet()));
        Map<Long, List<TagEntity>> tagMap = loadTagMap(
                posts.stream().map(PostEntity::getId).collect(Collectors.toSet()));
        Map<Long, UserEntity> authorMap = loadAuthorMap(
                posts.stream().map(PostEntity::getAuthorId).collect(Collectors.toSet()));

        List<PostSummaryResponse> responses = new ArrayList<>(posts.size());
        for (PostEntity post : posts) {
            responses.add(buildSummary(post, categoryMap, tagMap, authorMap));
        }
        return responses;
    }

    private PostDetailResponse buildDetail(PostEntity post,
                                           Map<Long, CategoryEntity> categoryMap,
                                           Map<Long, List<TagEntity>> tagMap,
                                           Map<Long, UserEntity> authorMap) {
        CategoryEntity category = categoryMap.get(post.getCategoryId());
        if (category == null) {
            throw new BusinessException(ResponseCode.CATEGORY_NOT_FOUND);
        }

        PostAuthorResponse author = toAuthor(authorMap.get(post.getAuthorId()), post.getAuthorId());
        List<TagResponse> tags = tagMap.getOrDefault(post.getId(), List.of())
                .stream()
                .map(TagResponse::from)
                .toList();

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getContentType(),
                post.getCoverImageUrl(),
                author,
                CategoryResponse.from(category),
                tags,
                post.getVisibility(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getFavoriteCount(),
                postScoreCalculator.calculateHotScore(
                        post.getLikeCount(), post.getCommentCount(), post.getFavoriteCount(), post.getPublishedAt()),
                post.getPublishedAt(),
                post.getUpdatedAt()
        );
    }

    private PostSummaryResponse buildSummary(PostEntity post,
                                             Map<Long, CategoryEntity> categoryMap,
                                             Map<Long, List<TagEntity>> tagMap,
                                             Map<Long, UserEntity> authorMap) {
        CategoryEntity category = categoryMap.get(post.getCategoryId());
        if (category == null) {
            throw new BusinessException(ResponseCode.CATEGORY_NOT_FOUND);
        }

        return new PostSummaryResponse(
                post.getId(),
                post.getTitle(),
                buildExcerpt(post.getContent()),
                post.getCoverImageUrl(),
                toAuthor(authorMap.get(post.getAuthorId()), post.getAuthorId()),
                CategoryResponse.from(category),
                tagMap.getOrDefault(post.getId(), List.of()).stream().map(TagResponse::from).toList(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getFavoriteCount(),
                postScoreCalculator.calculateHotScore(
                        post.getLikeCount(), post.getCommentCount(), post.getFavoriteCount(), post.getPublishedAt()),
                post.getPublishedAt()
        );
    }

    private Map<Long, CategoryEntity> loadCategoryMap(Collection<Long> categoryIds) {
        if (categoryIds.isEmpty()) {
            return Map.of();
        }
        return categoryRepository.findAllById(categoryIds)
                .stream()
                .collect(Collectors.toMap(CategoryEntity::getId, category -> category));
    }

    private Map<Long, UserEntity> loadAuthorMap(Collection<Long> authorIds) {
        if (authorIds.isEmpty()) {
            return Map.of();
        }
        return userRepository.findAllById(authorIds)
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, user -> user));
    }

    private Map<Long, List<TagEntity>> loadTagMap(Collection<Long> postIds) {
        if (postIds.isEmpty()) {
            return Map.of();
        }

        List<PostTagEntity> postTags = postTagRepository.findByPostIdIn(postIds);
        if (postTags.isEmpty()) {
            return Map.of();
        }

        Set<Long> tagIds = postTags.stream().map(PostTagEntity::getTagId).collect(Collectors.toSet());
        Map<Long, TagEntity> tagById = tagRepository.findAllById(tagIds)
                .stream()
                .collect(Collectors.toMap(TagEntity::getId, tag -> tag));

        Map<Long, List<TagEntity>> tagsByPostId = new LinkedHashMap<>();
        for (PostTagEntity postTag : postTags) {
            TagEntity tag = tagById.get(postTag.getTagId());
            if (tag == null) {
                continue;
            }
            tagsByPostId.computeIfAbsent(postTag.getPostId(), ignored -> new ArrayList<>()).add(tag);
        }

        Map<Long, List<TagEntity>> sorted = new HashMap<>(tagsByPostId.size());
        tagsByPostId.forEach((postId, tags) -> {
            List<TagEntity> sortedTags = tags.stream()
                    .sorted(Comparator.comparing(TagEntity::getName))
                    .toList();
            sorted.put(postId, sortedTags);
        });
        return sorted;
    }

    private PostAuthorResponse toAuthor(UserEntity user, Long authorId) {
        return user == null ? PostAuthorResponse.fallback(authorId) : PostAuthorResponse.from(user);
    }

    private String buildExcerpt(String content) {
        if (content == null || content.isBlank()) {
            return "";
        }
        String normalized = content.replace('\n', ' ').replace('\r', ' ').trim();
        if (normalized.length() <= 140) {
            return normalized;
        }
        return normalized.substring(0, 137) + "...";
    }
}
