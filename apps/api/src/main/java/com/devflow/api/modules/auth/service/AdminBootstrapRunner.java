package com.devflow.api.modules.auth.service;

import com.devflow.api.modules.auth.config.AdminBootstrapProperties;
import com.devflow.api.modules.auth.entity.AdminStatus;
import com.devflow.api.modules.auth.entity.AdminUserEntity;
import com.devflow.api.modules.auth.repository.AdminUserRepository;
import java.time.LocalDateTime;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private final AdminBootstrapProperties adminBootstrapProperties;
    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrapRunner(AdminBootstrapProperties adminBootstrapProperties,
                                AdminUserRepository adminUserRepository,
                                PasswordEncoder passwordEncoder) {
        this.adminBootstrapProperties = adminBootstrapProperties;
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!adminBootstrapProperties.isEnabled()) {
            return;
        }

        String username = adminBootstrapProperties.getUsername();
        String password = adminBootstrapProperties.getPassword();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return;
        }
        String displayName = adminBootstrapProperties.getDisplayName();
        if (displayName == null || displayName.isBlank()) {
            displayName = username;
        }

        adminUserRepository.findByUsernameIgnoreCase(username).ifPresentOrElse(
                existing -> {
                },
                () -> {
                    LocalDateTime now = LocalDateTime.now();
                    AdminUserEntity admin = new AdminUserEntity();
                    admin.setUsername(username.trim());
                    admin.setPasswordHash(passwordEncoder.encode(password));
                    admin.setDisplayName(displayName.trim());
                    admin.setStatus(AdminStatus.ACTIVE);
                    admin.setCreatedAt(now);
                    admin.setUpdatedAt(now);
                    adminUserRepository.save(admin);
                }
        );
    }
}
