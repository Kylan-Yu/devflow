package com.devflow.api.modules.notification.repository;

import com.devflow.api.modules.notification.entity.NotificationEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    Optional<NotificationEntity> findByIdAndReceiverId(Long id, Long receiverId);

    List<NotificationEntity> findTop100ByReceiverIdOrderByCreatedAtDesc(Long receiverId);

    @Modifying
    @Query("""
            UPDATE NotificationEntity n
            SET n.isRead = true, n.readAt = :readAt
            WHERE n.receiverId = :receiverId
              AND n.isRead = false
            """)
    int markAllRead(@Param("receiverId") Long receiverId, @Param("readAt") LocalDateTime readAt);
}
