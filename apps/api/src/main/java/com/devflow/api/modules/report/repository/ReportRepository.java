package com.devflow.api.modules.report.repository;

import com.devflow.api.modules.report.entity.ReportEntity;
import com.devflow.api.modules.report.entity.ReportStatus;
import com.devflow.api.modules.report.entity.ReportTargetType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    Optional<ReportEntity> findByReporterIdAndTargetTypeAndTargetIdAndStatus(Long reporterId,
                                                                             ReportTargetType targetType,
                                                                             Long targetId,
                                                                             ReportStatus status);

    Page<ReportEntity> findByReporterId(Long reporterId, Pageable pageable);

    Page<ReportEntity> findByStatus(ReportStatus status, Pageable pageable);

    long countByStatus(ReportStatus status);
}
