package com.devflow.api.modules.interaction.repository;

import com.devflow.api.modules.interaction.entity.PostFavoriteEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostFavoriteRepository extends JpaRepository<PostFavoriteEntity, Long> {

    Optional<PostFavoriteEntity> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
