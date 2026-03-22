package com.devflow.api.modules.user.repository;

import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserRole;
import com.devflow.api.modules.user.entity.UserStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsernameIgnoreCase(String username);

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByIdAndStatus(Long id, UserStatus status);

    Page<UserEntity> findByRole(UserRole role, Pageable pageable);

    @Query("SELECT u FROM UserEntity u WHERE u.role = :role AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(u.displayName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<UserEntity> findByRoleAndSearch(UserRole role, String search, Pageable pageable);

    long countByRole(UserRole role);

    long countByRoleAndStatus(UserRole role, UserStatus status);
}
