package com.devflow.api.modules.interaction.repository;

import com.devflow.api.modules.interaction.entity.UserFollowEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollowEntity, Long> {

    Optional<UserFollowEntity> findByFollowerIdAndFollowingId(Long followerId, Long followingId);

    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
}
