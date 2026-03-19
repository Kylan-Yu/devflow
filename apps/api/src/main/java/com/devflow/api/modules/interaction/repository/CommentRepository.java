package com.devflow.api.modules.interaction.repository;

import com.devflow.api.modules.interaction.entity.CommentEntity;
import com.devflow.api.modules.interaction.entity.CommentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    Optional<CommentEntity> findByIdAndDeletedAtIsNull(Long id);

    List<CommentEntity> findTop200ByPostIdAndStatusAndDeletedAtIsNullOrderByCreatedAtDesc(Long postId, CommentStatus status);
}
