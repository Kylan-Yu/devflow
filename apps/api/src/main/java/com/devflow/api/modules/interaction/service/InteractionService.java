package com.devflow.api.modules.interaction.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.modules.feed.cache.FeedPageCache;
import com.devflow.api.modules.interaction.dto.request.CreateCommentRequest;
import com.devflow.api.modules.interaction.dto.response.CommentResponse;
import com.devflow.api.modules.interaction.dto.response.FollowStatusResponse;
import com.devflow.api.modules.interaction.dto.response.PostInteractionStateResponse;
import com.devflow.api.modules.interaction.dto.response.PostInteractionSummaryResponse;
import com.devflow.api.modules.interaction.entity.CommentEntity;
import com.devflow.api.modules.interaction.entity.CommentStatus;
import com.devflow.api.modules.interaction.entity.PostFavoriteEntity;
import com.devflow.api.modules.interaction.entity.PostLikeEntity;
import com.devflow.api.modules.interaction.entity.UserFollowEntity;
import com.devflow.api.modules.interaction.repository.CommentRepository;
import com.devflow.api.modules.interaction.repository.PostFavoriteRepository;
import com.devflow.api.modules.interaction.repository.PostLikeRepository;
import com.devflow.api.modules.interaction.repository.UserFollowRepository;
import com.devflow.api.modules.notification.event.InteractionEventType;
import com.devflow.api.modules.notification.event.InteractionNotificationEvent;
import com.devflow.api.modules.notification.event.NotificationEventPublisher;
import com.devflow.api.modules.notification.event.NotificationEventRouting;
import com.devflow.api.modules.notification.event.NotificationTargetType;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.entity.PostStatus;
import com.devflow.api.modules.post.entity.PostVisibility;
import com.devflow.api.modules.post.repository.PostRepository;
import com.devflow.api.modules.post.service.PostScoreCalculator;
import com.devflow.api.modules.post.service.PostService;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserStatus;
import com.devflow.api.modules.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InteractionService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostFavoriteRepository postFavoriteRepository;
    private final CommentRepository commentRepository;
    private final UserFollowRepository userFollowRepository;
    private final PostScoreCalculator postScoreCalculator;
    private final FeedPageCache feedPageCache;
    private final NotificationEventPublisher notificationEventPublisher;
    private final PostService postService;

    public InteractionService(PostRepository postRepository,
                              UserRepository userRepository,
                              PostLikeRepository postLikeRepository,
                              PostFavoriteRepository postFavoriteRepository,
                              CommentRepository commentRepository,
                              UserFollowRepository userFollowRepository,
                              PostScoreCalculator postScoreCalculator,
                              FeedPageCache feedPageCache,
                              NotificationEventPublisher notificationEventPublisher,
                              PostService postService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.postLikeRepository = postLikeRepository;
        this.postFavoriteRepository = postFavoriteRepository;
        this.commentRepository = commentRepository;
        this.userFollowRepository = userFollowRepository;
        this.postScoreCalculator = postScoreCalculator;
        this.feedPageCache = feedPageCache;
        this.notificationEventPublisher = notificationEventPublisher;
        this.postService = postService;
    }

    @Transactional
    public PostInteractionSummaryResponse likePost(Long userId, Long postId) {
        loadActiveUser(userId);
        PostEntity post = loadPublishedPost(postId);

        if (postLikeRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new BusinessException(ResponseCode.POST_ALREADY_LIKED);
        }

        LocalDateTime now = LocalDateTime.now();
        PostLikeEntity postLike = new PostLikeEntity();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLike.setCreatedAt(now);
        postLikeRepository.save(postLike);

        applyPostCounterChange(post, 1, 0, 0, now);
        publishPostLikeEvent(userId, post);
        return toSummary(post);
    }

    @Transactional
    public PostInteractionSummaryResponse unlikePost(Long userId, Long postId) {
        loadActiveUser(userId);
        PostEntity post = loadPublishedPost(postId);

        PostLikeEntity like = postLikeRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.POST_NOT_LIKED));
        postLikeRepository.delete(like);

        applyPostCounterChange(post, -1, 0, 0, LocalDateTime.now());
        return toSummary(post);
    }

    @Transactional
    public PostInteractionSummaryResponse favoritePost(Long userId, Long postId) {
        loadActiveUser(userId);
        PostEntity post = loadPublishedPost(postId);

        if (postFavoriteRepository.existsByPostIdAndUserId(postId, userId)) {
            throw new BusinessException(ResponseCode.POST_ALREADY_FAVORITED);
        }

        PostFavoriteEntity favorite = new PostFavoriteEntity();
        favorite.setPostId(postId);
        favorite.setUserId(userId);
        favorite.setCreatedAt(LocalDateTime.now());
        postFavoriteRepository.save(favorite);

        applyPostCounterChange(post, 0, 0, 1, LocalDateTime.now());
        return toSummary(post);
    }

    @Transactional
    public PostInteractionSummaryResponse unfavoritePost(Long userId, Long postId) {
        loadActiveUser(userId);
        PostEntity post = loadPublishedPost(postId);

        PostFavoriteEntity favorite = postFavoriteRepository.findByPostIdAndUserId(postId, userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.POST_NOT_FAVORITED));
        postFavoriteRepository.delete(favorite);

        applyPostCounterChange(post, 0, 0, -1, LocalDateTime.now());
        return toSummary(post);
    }

    @Transactional(readOnly = true)
    public PostInteractionStateResponse postInteractionState(Long userId, Long postId) {
        loadActiveUser(userId);
        loadPublishedPost(postId);
        return new PostInteractionStateResponse(
                postLikeRepository.existsByPostIdAndUserId(postId, userId),
                postFavoriteRepository.existsByPostIdAndUserId(postId, userId)
        );
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> listComments(Long postId) {
        loadPublishedPost(postId);
        List<CommentEntity> comments =
                commentRepository.findTop200ByPostIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        postId, CommentStatus.ACTIVE);

        Set<Long> authorIds = comments.stream().map(CommentEntity::getUserId).collect(Collectors.toSet());
        Map<Long, String> displayNameMap = userRepository.findAllById(authorIds)
                .stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getDisplayName));

        return comments.stream()
                .map(comment -> CommentResponse.from(
                        comment, displayNameMap.getOrDefault(comment.getUserId(), "Unknown User")))
                .toList();
    }

    @Transactional
    public CommentResponse createComment(Long userId, Long postId, CreateCommentRequest request) {
        UserEntity actor = loadActiveUser(userId);
        PostEntity post = loadPublishedPost(postId);

        LocalDateTime now = LocalDateTime.now();
        CommentEntity comment = new CommentEntity();
        comment.setPostId(postId);
        comment.setUserId(userId);
        comment.setParentId(null);
        comment.setContent(request.content().trim());
        comment.setStatus(CommentStatus.ACTIVE);
        comment.setCreatedAt(now);

        CommentEntity saved = commentRepository.save(comment);
        applyPostCounterChange(post, 0, 1, 0, now);
        publishPostCommentedEvent(actor, post, saved);

        return CommentResponse.from(saved, actor.getDisplayName());
    }

    @Transactional
    public PostInteractionSummaryResponse deleteComment(Long userId, Long commentId) {
        loadActiveUser(userId);
        CommentEntity comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
                .orElseThrow(() -> new BusinessException(ResponseCode.COMMENT_NOT_FOUND));
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException(ResponseCode.COMMENT_DELETE_FORBIDDEN);
        }

        comment.setStatus(CommentStatus.DELETED);
        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);

        PostEntity post = loadPublishedPost(comment.getPostId());
        applyPostCounterChange(post, 0, -1, 0, LocalDateTime.now());
        return toSummary(post);
    }

    @Transactional
    public FollowStatusResponse follow(Long userId, Long targetUserId) {
        UserEntity actor = loadActiveUser(userId);
        UserEntity target = loadActiveUser(targetUserId);

        if (userId.equals(targetUserId)) {
            throw new BusinessException(ResponseCode.FOLLOW_SELF_FORBIDDEN);
        }
        if (userFollowRepository.existsByFollowerIdAndFollowingId(userId, targetUserId)) {
            throw new BusinessException(ResponseCode.USER_ALREADY_FOLLOWING);
        }

        UserFollowEntity follow = new UserFollowEntity();
        follow.setFollowerId(userId);
        follow.setFollowingId(targetUserId);
        follow.setCreatedAt(LocalDateTime.now());
        userFollowRepository.save(follow);

        publishFollowEvent(actor, target);
        return new FollowStatusResponse(true);
    }

    @Transactional
    public FollowStatusResponse unfollow(Long userId, Long targetUserId) {
        loadActiveUser(userId);
        loadActiveUser(targetUserId);

        UserFollowEntity follow = userFollowRepository.findByFollowerIdAndFollowingId(userId, targetUserId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOLLOWING));
        userFollowRepository.delete(follow);
        return new FollowStatusResponse(false);
    }

    @Transactional(readOnly = true)
    public FollowStatusResponse followStatus(Long userId, Long targetUserId) {
        loadActiveUser(userId);
        loadActiveUser(targetUserId);
        if (userId.equals(targetUserId)) {
            return new FollowStatusResponse(false);
        }
        return new FollowStatusResponse(userFollowRepository.existsByFollowerIdAndFollowingId(userId, targetUserId));
    }

    private void publishPostLikeEvent(Long actorId, PostEntity post) {
        if (actorId.equals(post.getAuthorId())) {
            return;
        }
        InteractionNotificationEvent event = new InteractionNotificationEvent(
                UUID.randomUUID().toString(),
                InteractionEventType.POST_LIKED,
                actorId,
                post.getAuthorId(),
                NotificationTargetType.POST,
                post.getId(),
                "notification.like_received",
                post.getTitle(),
                LocalDateTime.now()
        );
        publishAfterCommit(NotificationEventRouting.ROUTING_KEY_LIKE, event);
    }

    private void publishPostCommentedEvent(UserEntity actor, PostEntity post, CommentEntity comment) {
        if (actor.getId().equals(post.getAuthorId())) {
            return;
        }
        InteractionNotificationEvent event = new InteractionNotificationEvent(
                UUID.randomUUID().toString(),
                InteractionEventType.POST_COMMENTED,
                actor.getId(),
                post.getAuthorId(),
                NotificationTargetType.POST,
                post.getId(),
                "notification.comment_received",
                shortenPreview(comment.getContent()),
                LocalDateTime.now()
        );
        publishAfterCommit(NotificationEventRouting.ROUTING_KEY_COMMENT, event);
    }

    private void publishFollowEvent(UserEntity actor, UserEntity target) {
        InteractionNotificationEvent event = new InteractionNotificationEvent(
                UUID.randomUUID().toString(),
                InteractionEventType.USER_FOLLOWED,
                actor.getId(),
                target.getId(),
                NotificationTargetType.USER,
                target.getId(),
                "notification.follow_received",
                actor.getDisplayName(),
                LocalDateTime.now()
        );
        publishAfterCommit(NotificationEventRouting.ROUTING_KEY_FOLLOW, event);
    }

    private void publishAfterCommit(String routingKey, InteractionNotificationEvent event) {
        // EN: Interaction writes are committed before notification event publish.
        notificationEventPublisher.publishAfterCommit(routingKey, event);
    }

    private UserEntity loadActiveUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private PostEntity loadPublishedPost(Long postId) {
        PostEntity post = postRepository.findByIdAndStatusAndDeletedAtIsNull(postId, PostStatus.PUBLISHED)
                .orElseThrow(() -> new BusinessException(ResponseCode.POST_NOT_FOUND));
        if (post.getVisibility() != PostVisibility.PUBLIC) {
            throw new BusinessException(ResponseCode.POST_NOT_FOUND);
        }
        return post;
    }

    private void applyPostCounterChange(PostEntity post,
                                        int likeDelta,
                                        int commentDelta,
                                        int favoriteDelta,
                                        LocalDateTime now) {
        post.setLikeCount(Math.max(0, post.getLikeCount() + likeDelta));
        post.setCommentCount(Math.max(0, post.getCommentCount() + commentDelta));
        post.setFavoriteCount(Math.max(0, post.getFavoriteCount() + favoriteDelta));
        post.setScore(postScoreCalculator.calculateHotScore(
                post.getLikeCount(),
                post.getCommentCount(),
                post.getFavoriteCount(),
                post.getPublishedAt()
        ));
        post.setUpdatedAt(now);
        postRepository.save(post);
        feedPageCache.evictAll();
        postService.evictPostDetailCache(post.getId());
    }

    private PostInteractionSummaryResponse toSummary(PostEntity post) {
        return new PostInteractionSummaryResponse(
                post.getId(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getFavoriteCount(),
                post.getScore()
        );
    }

    private String shortenPreview(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String normalized = value.trim().replace('\n', ' ').replace('\r', ' ');
        if (normalized.length() <= 120) {
            return normalized;
        }
        return normalized.substring(0, 117) + "...";
    }
}
