package com.devflow.api.modules.auth.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.common.security.PrincipalType;
import com.devflow.api.modules.auth.dto.request.AdminLoginRequest;
import com.devflow.api.modules.auth.dto.response.AdminLoginResponse;
import com.devflow.api.modules.auth.entity.AdminStatus;
import com.devflow.api.modules.auth.entity.AdminUserEntity;
import com.devflow.api.modules.auth.repository.AdminUserRepository;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminAuthService {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminAuthService(AdminUserRepository adminUserRepository,
                            PasswordEncoder passwordEncoder,
                            JwtService jwtService) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AdminLoginResponse login(AdminLoginRequest request) {
        AdminUserEntity admin = adminUserRepository
                .findByUsernameIgnoreCaseAndStatus(request.username().trim(), AdminStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ResponseCode.ADMIN_INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), admin.getPasswordHash())) {
            throw new BusinessException(ResponseCode.ADMIN_INVALID_CREDENTIALS);
        }

        admin.setLastLoginAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        JwtService.IssuedToken accessToken = jwtService.issueAccessToken(admin.getId(), PrincipalType.ADMIN, "ADMIN");
        return new AdminLoginResponse(
                accessToken.token(),
                accessToken.expiresAt(),
                admin.getId(),
                admin.getUsername(),
                admin.getDisplayName()
        );
    }
}
