package com.devflow.api.modules.user.service;

import com.devflow.api.common.api.ResponseCode;
import com.devflow.api.common.cache.CacheKeyBuilder;
import com.devflow.api.common.cache.RedisCacheClient;
import com.devflow.api.common.exception.BusinessException;
import com.devflow.api.modules.user.dto.request.UpdateProfileRequest;
import com.devflow.api.modules.user.dto.response.UserProfileResponse;
import com.devflow.api.modules.user.entity.UserEntity;
import com.devflow.api.modules.user.entity.UserStatus;
import com.devflow.api.modules.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private static final Duration USER_PROFILE_CACHE_TTL = Duration.ofMinutes(10);

    private final UserRepository userRepository;
    private final RedisCacheClient redisCacheClient;

    public UserService(UserRepository userRepository, RedisCacheClient redisCacheClient) {
        this.userRepository = userRepository;
        this.redisCacheClient = redisCacheClient;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getById(Long userId) {
        return getCachedProfile(userId);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUser(Long userId) {
        return getCachedProfile(userId);
    }

    @Transactional
    public UserProfileResponse updateCurrentUser(Long userId, UpdateProfileRequest request) {
        UserEntity user = loadActiveUser(userId);

        if (request.displayName() != null && !request.displayName().isBlank()) {
            user.setDisplayName(request.displayName().trim());
        }
        if (request.bio() != null) {
            user.setBio(request.bio().trim());
        }
        if (request.avatarUrl() != null) {
            user.setAvatarUrl(trimNullable(request.avatarUrl()));
        }
        if (request.preferredLanguage() != null) {
            user.setPreferredLanguage(request.preferredLanguage());
        }

        user.setUpdatedAt(LocalDateTime.now());
        UserProfileResponse profile = UserProfileResponse.from(userRepository.save(user));
        redisCacheClient.evict(CacheKeyBuilder.userProfile(userId));
        return profile;
    }

    private UserEntity loadActiveUser(Long userId) {
        return userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private UserProfileResponse getCachedProfile(Long userId) {
        String cacheKey = CacheKeyBuilder.userProfile(userId);
        var cached = redisCacheClient.get(cacheKey, UserProfileResponse.class);
        if (cached.isPresent()) {
            return cached.get();
        }

        UserProfileResponse profile = UserProfileResponse.from(loadActiveUser(userId));
        // EN: User profile basic info changes infrequently, so a longer TTL is safe.
        redisCacheClient.set(cacheKey, profile, USER_PROFILE_CACHE_TTL);
        return profile;
    }

    private String trimNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
