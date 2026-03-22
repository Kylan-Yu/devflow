package com.devflow.api.modules.admin.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.cache.CacheKeyBuilder;
import com.devflow.api.common.cache.RedisCacheClient;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.modules.admin.dto.response.AdminPageResponse;
import com.devflow.api.modules.admin.dto.response.AdminDashboardOverviewResponse;
import com.devflow.api.modules.admin.dto.response.AdminPostSummaryResponse;
import com.devflow.api.modules.admin.dto.response.AdminUserSummaryResponse;
import com.devflow.api.modules.admin.dto.response.AdminAuditLogResponse;
import com.devflow.api.modules.admin.entity.AdminAuditLogEntity;
import com.devflow.api.modules.admin.repository.AdminAuditLogRepository;
import com.devflow.api.modules.auth.entity.AdminUserEntity;
import com.devflow.api.modules.auth.repository.AdminUserRepository;
import com.devflow.api.modules.feed.cache.FeedPageCache;
import com.devflow.api.modules.post.entity.CategoryEntity;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.entity.PostStatus;
import com.devflow.api.modules.post.repository.CategoryRepository;
import com.devflow.api.modules.post.repository.PostRepository;
import com.devflow.api.modules.report.entity.ReportStatus;
import com.devflow.api.modules.report.repository.ReportRepository;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserRole;
import com.devflow.api.modules.user.entity.UserStatus;
import com.devflow.api.modules.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminModerationService {

    private static final int DEFAULT_LIST_SIZE = 12;
    private static final int MAX_LIST_SIZE = 50;

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final ReportRepository reportRepository;
    private final AdminAuditLogRepository adminAuditLogRepository;
    private final AdminUserRepository adminUserRepository;
    private final FeedPageCache feedPageCache;
    private final RedisCacheClient redisCacheClient;
    private final AdminAuditLogService adminAuditLogService;

    public AdminModerationService(UserRepository userRepository,
                                  PostRepository postRepository,
                                  CategoryRepository categoryRepository,
                                  ReportRepository reportRepository,
                                  AdminAuditLogRepository adminAuditLogRepository,
                                  AdminUserRepository adminUserRepository,
                                  FeedPageCache feedPageCache,
                                  RedisCacheClient redisCacheClient,
                                  AdminAuditLogService adminAuditLogService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.reportRepository = reportRepository;
        this.adminAuditLogRepository = adminAuditLogRepository;
        this.adminUserRepository = adminUserRepository;
        this.feedPageCache = feedPageCache;
        this.redisCacheClient = redisCacheClient;
        this.adminAuditLogService = adminAuditLogService;
    }

    @Transactional(readOnly = true)
    public AdminDashboardOverviewResponse getOverview() {
        return new AdminDashboardOverviewResponse(
                userRepository.countByRole(UserRole.USER),
                userRepository.countByRoleAndStatus(UserRole.USER, UserStatus.ACTIVE),
                userRepository.countByRoleAndStatus(UserRole.USER, UserStatus.DISABLED),
                postRepository.countByStatus(PostStatus.PUBLISHED),
                postRepository.countByStatus(PostStatus.DELETED),
                reportRepository.countByStatus(ReportStatus.PENDING),
                reportRepository.countByStatus(ReportStatus.RESOLVED)
        );
    }

    @Transactional(readOnly = true)
    public List<AdminUserSummaryResponse> listUsers(int size) {
        PageRequest pageRequest = PageRequest.of(
                0,
                normalizeSize(size),
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"))
        );

        return userRepository.findByRole(UserRole.USER, pageRequest)
                .stream()
                .map(AdminUserSummaryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminPageResponse<AdminUserSummaryResponse> listUsers(int page, int size, String search) {
        int actualPage = page > 0 ? page - 1 : 0;
        int actualSize = normalizeSize(size);
        
        PageRequest pageRequest = PageRequest.of(
                actualPage,
                actualSize,
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"))
        );
        
        Page<UserEntity> userPage;
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.findByRoleAndSearch(UserRole.USER, search.trim(), pageRequest);
        } else {
            userPage = userRepository.findByRole(UserRole.USER, pageRequest);
        }
        
        List<AdminUserSummaryResponse> items = userPage.getContent()
                .stream()
                .map(AdminUserSummaryResponse::from)
                .toList();
                
        return AdminPageResponse.of(items, page, actualSize, userPage.getTotalElements());
    }

    @Transactional
    public AdminUserSummaryResponse updateUserStatus(Long adminId, Long userId, UserStatus status) {
        UserEntity user = userRepository.findById(userId)
                .filter(item -> item.getRole() == UserRole.USER)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        if (user.getStatus() == status) {
            return AdminUserSummaryResponse.from(user);
        }

        UserStatus previousStatus = user.getStatus();
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());

        UserEntity savedUser = userRepository.save(user);
        redisCacheClient.evict(CacheKeyBuilder.userProfile(savedUser.getId()));
        adminAuditLogService.recordUserStatusChange(adminId, savedUser, previousStatus, status);
        return AdminUserSummaryResponse.from(savedUser);
    }

    @Transactional(readOnly = true)
    public List<AdminPostSummaryResponse> listPosts(int size) {
        PageRequest pageRequest = PageRequest.of(
                0,
                normalizeSize(size),
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"))
        );

        List<PostEntity> posts = postRepository.findAll(pageRequest).getContent();
        return mapPostSummaries(posts);
    }

    @Transactional(readOnly = true)
    public AdminPageResponse<AdminPostSummaryResponse> listPosts(int page, int size, String search) {
        int actualPage = page > 0 ? page - 1 : 0;
        int actualSize = normalizeSize(size);
        
        PageRequest pageRequest = PageRequest.of(
                actualPage,
                actualSize,
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"))
        );
        
        Page<PostEntity> postPage;
        if (search != null && !search.trim().isEmpty()) {
            postPage = postRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(search.trim(), search.trim(), pageRequest);
        } else {
            postPage = postRepository.findAll(pageRequest);
        }
        
        List<AdminPostSummaryResponse> items = mapPostSummaries(postPage.getContent());
        
        return AdminPageResponse.of(items, page, actualSize, postPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<AdminAuditLogResponse> listAuditLogs(int size) {
        return adminAuditLogService.listRecentLogs(size);
    }

    @Transactional(readOnly = true)
    public AdminPageResponse<AdminAuditLogResponse> listAuditLogs(int page, int size, String search) {
        int actualPage = page > 0 ? page - 1 : 0;
        int actualSize = normalizeSize(size);
        
        PageRequest pageRequest = PageRequest.of(
                actualPage,
                actualSize,
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        );
        
        Page<AdminAuditLogEntity> logPage;
        if (search != null && !search.trim().isEmpty()) {
            logPage = adminAuditLogRepository.findByActionTypeContainingIgnoreCaseOrTargetTypeContainingIgnoreCaseOrTargetLabelContainingIgnoreCase(search.trim(), search.trim(), search.trim(), pageRequest);
        } else {
            logPage = adminAuditLogRepository.findAll(pageRequest);
        }
        
        Map<Long, AdminUserEntity> adminMap = loadAdminMap(logPage.getContent()
                .stream()
                .map(AdminAuditLogEntity::getAdminUserId)
                .collect(Collectors.toSet()));

        List<AdminAuditLogResponse> items = logPage.getContent()
                .stream()
                .map(log -> {
                    AdminUserEntity admin = adminMap.get(log.getAdminUserId());
                    return new AdminAuditLogResponse(
                            log.getId(),
                            admin == null ? "-" : admin.getUsername(),
                            admin == null ? "-" : admin.getDisplayName(),
                            log.getActionType(),
                            log.getTargetType(),
                            log.getTargetId(),
                            log.getTargetLabel(),
                            log.getPreviousState(),
                            log.getNextState(),
                            log.getResolutionAction(),
                            log.getContextLabel(),
                            log.getCreatedAt()
                    );
                })
                .toList();
        
        return AdminPageResponse.of(items, page, actualSize, logPage.getTotalElements());
    }

    @Transactional
    public AdminPostSummaryResponse updatePostStatus(Long adminId, Long postId, PostStatus status) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ResponseCode.POST_NOT_FOUND));

        if (post.getStatus() != status) {
            PostStatus previousStatus = post.getStatus();
            LocalDateTime now = LocalDateTime.now();
            post.setStatus(status);
            post.setUpdatedAt(now);
            post.setDeletedAt(status == PostStatus.DELETED ? now : null);
            if (status == PostStatus.PUBLISHED && post.getPublishedAt() == null) {
                post.setPublishedAt(now);
            }
            post = postRepository.save(post);
            feedPageCache.evictAll();
            redisCacheClient.evict(CacheKeyBuilder.postDetail(postId));
            adminAuditLogService.recordPostStatusChange(adminId, post, previousStatus, status);
        }

        return mapPostSummaries(List.of(post)).get(0);
    }

    private List<AdminPostSummaryResponse> mapPostSummaries(List<PostEntity> posts) {
        if (posts.isEmpty()) {
            return List.of();
        }

        Map<Long, UserEntity> authorMap = loadUserMap(posts.stream()
                .map(PostEntity::getAuthorId)
                .collect(Collectors.toSet()));
        Map<Long, CategoryEntity> categoryMap = loadCategoryMap(posts.stream()
                .map(PostEntity::getCategoryId)
                .collect(Collectors.toSet()));

        return posts.stream()
                .map(post -> toPostSummary(post, authorMap.get(post.getAuthorId()), categoryMap.get(post.getCategoryId())))
                .toList();
    }

    private AdminPostSummaryResponse toPostSummary(PostEntity post,
                                                   UserEntity author,
                                                   CategoryEntity category) {
        return new AdminPostSummaryResponse(
                post.getId(),
                post.getTitle(),
                author == null ? "-" : author.getUsername(),
                author == null ? "-" : author.getDisplayName(),
                category == null ? "unknown" : category.getCode(),
                category == null ? "-" : category.getNameZh(),
                category == null ? "-" : category.getNameEn(),
                post.getStatus(),
                post.getLikeCount(),
                post.getCommentCount(),
                post.getFavoriteCount(),
                post.getPublishedAt(),
                post.getUpdatedAt(),
                post.getDeletedAt()
        );
    }

    private Map<Long, UserEntity> loadUserMap(Collection<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, user -> user));
    }

    private Map<Long, CategoryEntity> loadCategoryMap(Set<Long> categoryIds) {
        if (categoryIds.isEmpty()) {
            return Map.of();
        }

        return categoryRepository.findAllById(categoryIds).stream()
                .collect(Collectors.toMap(CategoryEntity::getId, category -> category));
    }

    private Map<Long, AdminUserEntity> loadAdminMap(Set<Long> adminIds) {
        if (adminIds.isEmpty()) {
            return Map.of();
        }

        return adminUserRepository.findAllById(adminIds).stream()
                .collect(Collectors.toMap(AdminUserEntity::getId, admin -> admin));
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_LIST_SIZE;
        }
        return Math.min(size, MAX_LIST_SIZE);
    }
}
