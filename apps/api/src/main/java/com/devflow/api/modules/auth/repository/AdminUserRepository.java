package com.devflow.api.modules.auth.repository;

import com.devflow.api.modules.auth.entity.AdminUserEntity;
import com.devflow.api.modules.auth.entity.AdminStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUserEntity, Long> {

    Optional<AdminUserEntity> findByUsernameIgnoreCaseAndStatus(String username, AdminStatus status);

    Optional<AdminUserEntity> findByUsernameIgnoreCase(String username);

    Optional<AdminUserEntity> findByIdAndStatus(Long id, AdminStatus status);
}
