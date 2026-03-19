package com.devflow.api.modules.auth.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.common.security.PrincipalType;
import com.devflow.api.modules.auth.dto.request.LoginRequest;
import com.devflow.api.modules.auth.dto.request.LogoutRequest;
import com.devflow.api.modules.auth.dto.request.RefreshTokenRequest;
import com.devflow.api.modules.auth.dto.request.RegisterRequest;
import com.devflow.api.modules.auth.dto.response.AuthSessionResponse;
import com.devflow.api.modules.auth.dto.response.AuthTokenResponse;
import com.devflow.api.modules.auth.entity.RefreshTokenEntity;
import com.devflow.api.modules.auth.repository.RefreshTokenRepository;
import com.devflow.api.modules.user.dto.response.UserProfileResponse;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserRole;
import com.devflow.api.modules.user.entity.UserStatus;
import com.devflow.api.modules.user.repository.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenHashService tokenHashService;

    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       TokenHashService tokenHashService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.tokenHashService = tokenHashService;
    }

    @Transactional
    public AuthSessionResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        String normalizedUsername = request.username().trim();

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new BusinessException(ResponseCode.AUTH_EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            throw new BusinessException(ResponseCode.AUTH_USERNAME_ALREADY_EXISTS);
        }

        LocalDateTime now = LocalDateTime.now();
        UserEntity user = new UserEntity();
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName().trim());
        user.setBio(null);
        user.setPreferredLanguage(request.preferredLanguage());
        user.setRole(UserRole.USER);
        user.setStatus(UserStatus.ACTIVE);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserEntity savedUser = userRepository.save(user);
        TokenBundle tokenBundle = issueAndPersistRefreshToken(savedUser.getId(), savedUser.getRole().name());

        return new AuthSessionResponse(
                tokenBundle.toResponse(),
                UserProfileResponse.from(savedUser)
        );
    }

    @Transactional
    public AuthSessionResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByEmailIgnoreCase(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BusinessException(ResponseCode.AUTH_INVALID_CREDENTIALS));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException(ResponseCode.AUTH_ACCOUNT_DISABLED);
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(ResponseCode.AUTH_INVALID_CREDENTIALS);
        }

        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        TokenBundle tokenBundle = issueAndPersistRefreshToken(user.getId(), user.getRole().name());
        return new AuthSessionResponse(tokenBundle.toResponse(), UserProfileResponse.from(user));
    }

    @Transactional
    public AuthSessionResponse refresh(RefreshTokenRequest request) {
        JwtService.JwtPayload payload;
        try {
            payload = jwtService.parseRefreshToken(request.refreshToken());
        } catch (ExpiredJwtException exception) {
            throw new BusinessException(ResponseCode.AUTH_REFRESH_TOKEN_EXPIRED);
        } catch (JwtException exception) {
            throw new BusinessException(ResponseCode.AUTH_INVALID_REFRESH_TOKEN);
        }

        if (payload.principalType() != PrincipalType.USER) {
            throw new BusinessException(ResponseCode.AUTH_INVALID_REFRESH_TOKEN);
        }

        String currentHash = tokenHashService.sha256(request.refreshToken());
        RefreshTokenEntity storedToken = refreshTokenRepository.findByTokenHash(currentHash)
                .orElseThrow(() -> new BusinessException(ResponseCode.AUTH_INVALID_REFRESH_TOKEN));

        if (storedToken.isRevoked()) {
            throw new BusinessException(ResponseCode.AUTH_INVALID_REFRESH_TOKEN);
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResponseCode.AUTH_REFRESH_TOKEN_EXPIRED);
        }

        UserEntity user = userRepository.findByIdAndStatus(payload.subjectId(), UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        TokenBundle tokenBundle = issueAndPersistRefreshToken(user.getId(), user.getRole().name());

        storedToken.setRevoked(true);
        storedToken.setRevokedAt(LocalDateTime.now());
        storedToken.setReplacedByHash(tokenBundle.refreshTokenHash());
        refreshTokenRepository.save(storedToken);

        return new AuthSessionResponse(tokenBundle.toResponse(), UserProfileResponse.from(user));
    }

    @Transactional
    public void logout(LogoutRequest request) {
        try {
            jwtService.parseRefreshToken(request.refreshToken());
        } catch (JwtException ignored) {
            return;
        }

        String hash = tokenHashService.sha256(request.refreshToken());
        refreshTokenRepository.findByTokenHash(hash).ifPresent(token -> {
            if (!token.isRevoked()) {
                token.setRevoked(true);
                token.setRevokedAt(LocalDateTime.now());
                refreshTokenRepository.save(token);
            }
        });
    }

    private TokenBundle issueAndPersistRefreshToken(Long subjectId, String role) {
        JwtService.IssuedToken accessToken = jwtService.issueAccessToken(subjectId, PrincipalType.USER, role);
        JwtService.IssuedToken refreshToken = jwtService.issueRefreshToken(subjectId, PrincipalType.USER, role);

        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.setPrincipalType(PrincipalType.USER);
        refreshTokenEntity.setSubjectId(subjectId);
        refreshTokenEntity.setTokenHash(tokenHashService.sha256(refreshToken.token()));
        refreshTokenEntity.setExpiresAt(LocalDateTime.ofInstant(refreshToken.expiresAt(), ZoneId.systemDefault()));
        refreshTokenEntity.setRevoked(false);
        refreshTokenEntity.setCreatedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshTokenEntity);

        return new TokenBundle(accessToken, refreshToken, refreshTokenEntity.getTokenHash());
    }

    private static final class TokenBundle {
        private final JwtService.IssuedToken accessToken;
        private final JwtService.IssuedToken refreshToken;
        private final String refreshTokenHash;

        private TokenBundle(JwtService.IssuedToken accessToken,
                            JwtService.IssuedToken refreshToken,
                            String refreshTokenHash) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.refreshTokenHash = refreshTokenHash;
        }

        private AuthTokenResponse toResponse() {
            return new AuthTokenResponse(
                    accessToken.token(),
                    accessToken.expiresAt(),
                    refreshToken.token(),
                    refreshToken.expiresAt()
            );
        }

        private String refreshTokenHash() {
            return refreshTokenHash;
        }
    }
}
