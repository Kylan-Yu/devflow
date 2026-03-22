package com.devflow.api.modules.admin.service;

import com.devflow.api.modules.admin.dto.response.AdminAuditLogResponse;
import com.devflow.api.modules.admin.entity.AdminAuditActionType;
import com.devflow.api.modules.admin.entity.AdminAuditLogEntity;
import com.devflow.api.modules.admin.entity.AdminAuditTargetType;
import com.devflow.api.modules.admin.repository.AdminAuditLogRepository;
import com.devflow.api.modules.auth.entity.AdminUserEntity;
import com.devflow.api.modules.auth.repository.AdminUserRepository;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.entity.PostStatus;
import com.devflow.api.modules.report.entity.ReportEntity;
import com.devflow.api.modules.report.entity.ReportResolutionAction;
import com.devflow.api.modules.report.entity.ReportStatus;
import com.devflow.api.modules.report.entity.ReportTargetType;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserStatus;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAuditLogService {

    private static final int DEFAULT_LIST_SIZE = 12;
    private static final int MAX_LIST_SIZE = 50;

    private final AdminAuditLogRepository adminAuditLogRepository;
    private final AdminUserRepository adminUserRepository;

    public AdminAuditLogService(AdminAuditLogRepository adminAuditLogRepository,
                                AdminUserRepository adminUserRepository) {
        this.adminAuditLogRepository = adminAuditLogRepository;
        this.adminUserRepository = adminUserRepository;
    }

    @Transactional
    public void recordUserStatusChange(Long adminId,
                                       UserEntity user,
                                       UserStatus previousStatus,
                                       UserStatus nextStatus) {
        record(
                adminId,
                AdminAuditActionType.USER_STATUS_UPDATED,
                AdminAuditTargetType.USER,
                user.getId(),
                user.getDisplayName(),
                previousStatus.name(),
                nextStatus.name(),
                null,
                null
        );
    }

    @Transactional
    public void recordPostStatusChange(Long adminId,
                                       PostEntity post,
                                       PostStatus previousStatus,
                                       PostStatus nextStatus) {
        record(
                adminId,
                AdminAuditActionType.POST_STATUS_UPDATED,
                AdminAuditTargetType.POST,
                post.getId(),
                post.getTitle(),
                previousStatus.name(),
                nextStatus.name(),
                null,
                null
        );
    }

    @Transactional
    public void recordReportReview(Long adminId,
                                   ReportEntity report,
                                   String targetLabel,
                                   ReportStatus previousStatus,
                                   ReportStatus nextStatus,
                                   ReportResolutionAction resolutionAction) {
        record(
                adminId,
                AdminAuditActionType.REPORT_REVIEWED,
                report.getTargetType() == ReportTargetType.POST ? AdminAuditTargetType.POST : AdminAuditTargetType.USER,
                report.getTargetId(),
                targetLabel,
                previousStatus.name(),
                nextStatus.name(),
                resolutionAction.name(),
                "Report #" + report.getId()
        );
    }

    @Transactional(readOnly = true)
    public List<AdminAuditLogResponse> listRecentLogs(int size) {
        PageRequest pageRequest = PageRequest.of(
                0,
                normalizeSize(size),
                Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id"))
        );

        List<AdminAuditLogEntity> logs = adminAuditLogRepository.findAll(pageRequest).getContent();
        Map<Long, AdminUserEntity> adminMap = loadAdminMap(logs.stream()
                .map(AdminAuditLogEntity::getAdminUserId)
                .collect(Collectors.toSet()));

        return logs.stream()
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
    }

    private void record(Long adminId,
                        AdminAuditActionType actionType,
                        AdminAuditTargetType targetType,
                        Long targetId,
                        String targetLabel,
                        String previousState,
                        String nextState,
                        String resolutionAction,
                        String contextLabel) {
        AdminAuditLogEntity log = new AdminAuditLogEntity();
        log.setAdminUserId(adminId);
        log.setActionType(actionType);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setTargetLabel(targetLabel);
        log.setPreviousState(previousState);
        log.setNextState(nextState);
        log.setResolutionAction(resolutionAction);
        log.setContextLabel(contextLabel);
        log.setCreatedAt(LocalDateTime.now());
        adminAuditLogRepository.save(log);
    }

    private Map<Long, AdminUserEntity> loadAdminMap(Collection<Long> adminIds) {
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
