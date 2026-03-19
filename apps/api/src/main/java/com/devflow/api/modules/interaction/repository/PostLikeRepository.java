package com.devflow.api.modules.interaction.repository;

import com.devflow.api.modules.interaction.entity.PostLikeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLikeEntity, Long> {

    Optional<PostLikeEntity> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
