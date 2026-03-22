package com.devflow.api.modules.report.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.cache.CacheKeyBuilder;
import com.devflow.api.common.cache.RedisCacheClient;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.modules.admin.service.AdminAuditLogService;
import com.devflow.api.modules.auth.entity.AdminStatus;
import com.devflow.api.modules.auth.entity.AdminUserEntity;
import com.devflow.api.modules.auth.repository.AdminUserRepository;
import com.devflow.api.modules.feed.cache.FeedPageCache;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.entity.PostStatus;
import com.devflow.api.modules.post.repository.PostRepository;
import com.devflow.api.modules.report.dto.request.CreateReportRequest;
import com.devflow.api.modules.report.dto.request.ReviewReportRequest;
import com.devflow.api.modules.report.dto.response.AdminReportSummaryResponse;
import com.devflow.api.modules.report.dto.response.ReportItemResponse;
import com.devflow.api.modules.report.entity.ReportEntity;
import com.devflow.api.modules.report.entity.ReportResolutionAction;
import com.devflow.api.modules.report.entity.ReportStatus;
import com.devflow.api.modules.report.entity.ReportTargetType;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

    private static final int DEFAULT_LIST_SIZE = 12;
    private static final int MAX_LIST_SIZE = 50;

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final AdminUserRepository adminUserRepository;
    private final FeedPageCache feedPageCache;
    private final RedisCacheClient redisCacheClient;
    private final AdminAuditLogService adminAuditLogService;

    public ReportService(ReportRepository reportRepository,
                         UserRepository userRepository,
                         PostRepository postRepository,
                         AdminUserRepository adminUserRepository,
                         FeedPageCache feedPageCache,
                         RedisCacheClient redisCacheClient,
                         AdminAuditLogService adminAuditLogService) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.adminUserRepository = adminUserRepository;
        this.feedPageCache = feedPageCache;
        this.redisCacheClient = redisCacheClient;
        this.adminAuditLogService = adminAuditLogService;
    }

    @Transactional
    public ReportItemResponse createPostReport(Long reporterId, Long postId, CreateReportRequest request) {
        UserEntity reporter = loadActiveReporter(reporterId);
        PostEntity post = postRepository.findByIdAndStatusAndDeletedAtIsNull(postId, PostStatus.PUBLISHED)
                .orElseThrow(() -> new BusinessException(ResponseCode.POST_NOT_FOUND));

        if (post.getAuthorId().equals(reporter.getId())) {
            throw new BusinessException(ResponseCode.REPORT_SELF_FORBIDDEN);
        }

        ensureNoPendingReport(reporterId, ReportTargetType.POST, postId);

        LocalDateTime now = LocalDateTime.now();
        ReportEntity report = new ReportEntity();
        report.setReporterId(reporterId);
        report.setTargetType(ReportTargetType.POST);
        report.setTargetId(postId);
        report.setReason(request.reason());
        report.setDetail(trimNullable(request.detail()));
        report.setStatus(ReportStatus.PENDING);
        report.setResolutionAction(ReportResolutionAction.NONE);
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        return toReportItem(reportRepository.save(report), post.getTitle(), post.getStatus().name());
    }

    @Transactional
    public ReportItemResponse createUserReport(Long reporterId, Long targetUserId, CreateReportRequest request) {
        loadActiveReporter(reporterId);
        UserEntity targetUser = userRepository.findByIdAndStatus(targetUserId, UserStatus.ACTIVE)
                .filter(item -> item.getRole() == UserRole.USER)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        if (reporterId.equals(targetUserId)) {
            throw new BusinessException(ResponseCode.REPORT_SELF_FORBIDDEN);
        }

        ensureNoPendingReport(reporterId, ReportTargetType.USER, targetUserId);

        LocalDateTime now = LocalDateTime.now();
        ReportEntity report = new ReportEntity();
        report.setReporterId(reporterId);
        report.setTargetType(ReportTargetType.USER);
        report.setTargetId(targetUserId);
        report.setReason(request.reason());
        report.setDetail(trimNullable(request.detail()));
        report.setStatus(ReportStatus.PENDING);
        report.setResolutionAction(ReportResolutionAction.NONE);
        report.setCreatedAt(now);
        report.setUpdatedAt(now);

        return toReportItem(reportRepository.save(report), targetUser.getDisplayName(), targetUser.getStatus().name());
    }

    @Transactional(readOnly = true)
    public List<ReportItemResponse> listMyReports(Long reporterId, int size) {
        PageRequest pageRequest = PageRequest.of(
                0,
                normalizeSize(size),
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        );

        List<ReportEntity> reports = reportRepository.findByReporterId(reporterId, pageRequest).getContent();
        if (reports.isEmpty()) {
            return List.of();
        }

        Map<Long, PostEntity> postMap = loadPostMap(extractTargetIds(reports, ReportTargetType.POST));
        Map<Long, UserEntity> userMap = loadUserMap(extractTargetIds(reports, ReportTargetType.USER));

        return reports.stream()
                .map(report -> toReportItem(
                        report,
                        resolveTargetLabel(report, postMap, userMap),
                        resolveTargetStatus(report, postMap, userMap)
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminReportSummaryResponse> listAdminReports(ReportStatus status, int size) {
        PageRequest pageRequest = PageRequest.of(
                0,
                normalizeSize(size),
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"))
        );

        List<ReportEntity> reports = status == null
                ? reportRepository.findAll(pageRequest).getContent()
                : reportRepository.findByStatus(status, pageRequest).getContent();

        return mapAdminReports(reports);
    }

    @Transactional
    public AdminReportSummaryResponse reviewReport(Long adminId, Long reportId, ReviewReportRequest request) {
        adminUserRepository.findByIdAndStatus(adminId, AdminStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ResponseCode.UNAUTHORIZED));

        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new BusinessException(ResponseCode.REPORT_NOT_FOUND));

        if (report.getStatus() != ReportStatus.PENDING) {
            throw new BusinessException(ResponseCode.REPORT_ALREADY_REVIEWED);
        }

        validateReviewRequest(report, request);

        ReportStatus previousStatus = report.getStatus();
        LocalDateTime now = LocalDateTime.now();
        if (request.status() == ReportStatus.RESOLVED) {
            applyResolutionAction(report, request.resolutionAction(), now);
        }

        report.setStatus(request.status());
        report.setResolutionAction(request.status() == ReportStatus.DISMISSED
                ? ReportResolutionAction.NONE
                : request.resolutionAction());
        report.setResolutionNote(trimNullable(request.resolutionNote()));
        report.setReviewedByAdminId(adminId);
        report.setReviewedAt(now);
        report.setUpdatedAt(now);

        ReportEntity savedReport = reportRepository.save(report);
        adminAuditLogService.recordReportReview(
                adminId,
                savedReport,
                resolveAuditTargetLabel(savedReport),
                previousStatus,
                savedReport.getStatus(),
                savedReport.getResolutionAction()
        );

        return mapAdminReports(List.of(savedReport)).get(0);
    }

    private UserEntity loadActiveReporter(Long reporterId) {
        return userRepository.findByIdAndStatus(reporterId, UserStatus.ACTIVE)
                .filter(item -> item.getRole() == UserRole.USER)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private void ensureNoPendingReport(Long reporterId, ReportTargetType targetType, Long targetId) {
        boolean exists = reportRepository
                .findByReporterIdAndTargetTypeAndTargetIdAndStatus(reporterId, targetType, targetId, ReportStatus.PENDING)
                .isPresent();
        if (exists) {
            throw new BusinessException(ResponseCode.REPORT_ALREADY_PENDING);
        }
    }

    private void validateReviewRequest(ReportEntity report, ReviewReportRequest request) {
        if (request.status() == ReportStatus.PENDING) {
            throw new BusinessException(ResponseCode.REPORT_INVALID_REVIEW);
        }

        if (request.status() == ReportStatus.DISMISSED && request.resolutionAction() != ReportResolutionAction.NONE) {
            throw new BusinessException(ResponseCode.REPORT_INVALID_REVIEW);
        }

        if (request.status() == ReportStatus.RESOLVED) {
            boolean invalidAction = report.getTargetType() == ReportTargetType.POST
                    ? request.resolutionAction() == ReportResolutionAction.DISABLE_USER
                    : request.resolutionAction() == ReportResolutionAction.HIDE_POST;
            if (invalidAction) {
                throw new BusinessException(ResponseCode.REPORT_INVALID_REVIEW);
            }
        }
    }

    private void applyResolutionAction(ReportEntity report, ReportResolutionAction action, LocalDateTime now) {
        if (action == ReportResolutionAction.HIDE_POST) {
            PostEntity post = postRepository.findById(report.getTargetId())
                    .orElseThrow(() -> new BusinessException(ResponseCode.POST_NOT_FOUND));
            if (post.getStatus() != PostStatus.DELETED) {
                post.setStatus(PostStatus.DELETED);
                post.setUpdatedAt(now);
                post.setDeletedAt(now);
                postRepository.save(post);
                feedPageCache.evictAll();
                redisCacheClient.evict(CacheKeyBuilder.postDetail(post.getId()));
            }
            return;
        }

        if (action == ReportResolutionAction.DISABLE_USER) {
            UserEntity user = userRepository.findById(report.getTargetId())
                    .filter(item -> item.getRole() == UserRole.USER)
                    .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
            if (user.getStatus() != UserStatus.DISABLED) {
                user.setStatus(UserStatus.DISABLED);
                user.setUpdatedAt(now);
                userRepository.save(user);
                redisCacheClient.evict(CacheKeyBuilder.userProfile(user.getId()));
            }
        }
    }

    private List<AdminReportSummaryResponse> mapAdminReports(List<ReportEntity> reports) {
        if (reports.isEmpty()) {
            return List.of();
        }

        Map<Long, UserEntity> reporterMap = loadUserMap(reports.stream()
                .map(ReportEntity::getReporterId)
                .collect(Collectors.toSet()));
        Map<Long, PostEntity> postMap = loadPostMap(extractTargetIds(reports, ReportTargetType.POST));
        Map<Long, UserEntity> userMap = loadUserMap(extractTargetIds(reports, ReportTargetType.USER));
        Map<Long, AdminUserEntity> adminMap = loadAdminMap(reports.stream()
                .map(ReportEntity::getReviewedByAdminId)
                .filter(id -> id != null)
                .collect(Collectors.toSet()));

        return reports.stream()
                .map(report -> {
                    UserEntity reporter = reporterMap.get(report.getReporterId());
                    AdminUserEntity reviewer = report.getReviewedByAdminId() == null
                            ? null
                            : adminMap.get(report.getReviewedByAdminId());

                    return new AdminReportSummaryResponse(
                            report.getId(),
                            report.getReporterId(),
                            reporter == null ? "-" : reporter.getUsername(),
                            reporter == null ? "-" : reporter.getDisplayName(),
                            report.getTargetType(),
                            report.getTargetId(),
                            resolveTargetLabel(report, postMap, userMap),
                            resolveTargetStatus(report, postMap, userMap),
                            report.getReason(),
                            report.getDetail(),
                            report.getStatus(),
                            report.getResolutionAction(),
                            report.getResolutionNote(),
                            reviewer == null ? null : reviewer.getDisplayName(),
                            report.getCreatedAt(),
                            report.getReviewedAt(),
                            report.getUpdatedAt()
                    );
                })
                .toList();
    }

    private Set<Long> extractTargetIds(List<ReportEntity> reports, ReportTargetType targetType) {
        return reports.stream()
                .filter(report -> report.getTargetType() == targetType)
                .map(ReportEntity::getTargetId)
                .collect(Collectors.toSet());
    }

    private Map<Long, UserEntity> loadUserMap(Collection<Long> userIds) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, user -> user));
    }

    private Map<Long, PostEntity> loadPostMap(Collection<Long> postIds) {
        if (postIds.isEmpty()) {
            return Map.of();
        }

        return postRepository.findAllById(postIds).stream()
                .collect(Collectors.toMap(PostEntity::getId, post -> post));
    }

    private Map<Long, AdminUserEntity> loadAdminMap(Collection<Long> adminIds) {
        if (adminIds.isEmpty()) {
            return Map.of();
        }

        return adminUserRepository.findAllById(adminIds).stream()
                .collect(Collectors.toMap(AdminUserEntity::getId, admin -> admin));
    }

    private String resolveTargetLabel(ReportEntity report,
                                      Map<Long, PostEntity> postMap,
                                      Map<Long, UserEntity> userMap) {
        if (report.getTargetType() == ReportTargetType.POST) {
            PostEntity post = postMap.get(report.getTargetId());
            return post == null ? "Post #" + report.getTargetId() : post.getTitle();
        }

        UserEntity user = userMap.get(report.getTargetId());
        return user == null ? "User #" + report.getTargetId() : user.getDisplayName();
    }

    private String resolveTargetStatus(ReportEntity report,
                                       Map<Long, PostEntity> postMap,
                                       Map<Long, UserEntity> userMap) {
        if (report.getTargetType() == ReportTargetType.POST) {
            PostEntity post = postMap.get(report.getTargetId());
            return post == null ? "UNKNOWN" : post.getStatus().name();
        }

        UserEntity user = userMap.get(report.getTargetId());
        return user == null ? "UNKNOWN" : user.getStatus().name();
    }

    private String resolveAuditTargetLabel(ReportEntity report) {
        if (report.getTargetType() == ReportTargetType.POST) {
            return postRepository.findById(report.getTargetId())
                    .map(PostEntity::getTitle)
                    .orElse("Post #" + report.getTargetId());
        }

        return userRepository.findById(report.getTargetId())
                .map(UserEntity::getDisplayName)
                .orElse("User #" + report.getTargetId());
    }

    private ReportItemResponse toReportItem(ReportEntity report, String targetLabel, String targetStatus) {
        return new ReportItemResponse(
                report.getId(),
                report.getTargetType(),
                report.getTargetId(),
                targetLabel,
                targetStatus,
                report.getReason(),
                report.getDetail(),
                report.getStatus(),
                report.getResolutionAction(),
                report.getResolutionNote(),
                report.getCreatedAt(),
                report.getReviewedAt()
        );
    }

    private int normalizeSize(int size) {
        if (size <= 0) {
            return DEFAULT_LIST_SIZE;
        }
        return Math.min(size, MAX_LIST_SIZE);
    }

    private String trimNullable(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
