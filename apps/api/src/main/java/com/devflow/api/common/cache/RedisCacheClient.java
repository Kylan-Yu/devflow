package com.devflow.api.common.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.util.Set;
import java.util.Optional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheClient {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheClient(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public <T> Optional<T> get(String key, Class<T> targetType) {
        String raw = safeGet(key);
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(raw, targetType));
        } catch (JsonProcessingException exception) {
            return Optional.empty();
        }
    }

    public <T> Optional<T> get(String key, TypeReference<T> typeReference) {
        String raw = safeGet(key);
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(raw, typeReference));
        } catch (JsonProcessingException exception) {
            return Optional.empty();
        }
    }

    public void set(String key, Object value, Duration ttl) {
        try {
            String encoded = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, encoded, ttl);
        } catch (Exception ignored) {
            // EN: Cache write failures should not break core request flow.
        }
    }

    public void evict(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception ignored) {
            // EN: Cache eviction failures are tolerated to keep writes resilient.
        }
    }

    public void evictPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys == null || keys.isEmpty()) {
                return;
            }
            redisTemplate.delete(keys);
        } catch (Exception ignored) {
            // EN: Pattern eviction is best-effort for lightweight cache strategy.
        }
    }

    private String safeGet(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception ignored) {
            return null;
        }
    }
}
