package com.devflow.api.modules.user.repository;

import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByIdAndStatus(Long id, UserStatus status);
}
