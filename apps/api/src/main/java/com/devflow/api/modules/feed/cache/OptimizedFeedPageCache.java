package com.devflow.api.modules.feed.cache;

import com.devflow.api.common.cache.CacheKeyBuilder;
import com.devflow.api.common.cache.RedisCacheClient;
import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class OptimizedFeedPageCache {

    private static final Duration HOT_CACHE_TTL = Duration.ofSeconds(30);
    private static final Duration NORMAL_CACHE_TTL = Duration.ofMinutes(2);
    private static final int HOT_POST_THRESHOLD = 50; // 热点帖子阈值
    
    private final FeedPageCache delegate;
    private final RedisCacheClient redisCacheClient;
    private final ScheduledExecutorService scheduler;
    
    // 热点检测
    private final Map<String, Long> accessCounters = new ConcurrentHashMap<>();
    private final Map<String, Long> lastAccessTime = new ConcurrentHashMap<>();

    public OptimizedFeedPageCache(FeedPageCache delegate, RedisCacheClient redisCacheClient) {
        this.delegate = delegate;
        this.redisCacheClient = redisCacheClient;
        this.scheduler = Executors.newScheduledThreadPool(2);
        
        // 定期清理访问计数器
        scheduler.scheduleAtFixedRate(this::resetCounters, 1, 1, TimeUnit.MINUTES);
        
        // 定期批量失效缓存
        scheduler.scheduleAtFixedRate(this::batchEvictHotFeeds, 30, 30, TimeUnit.SECONDS);
    }

    public Optional<CursorPageResponse<PostSummaryResponse>> get(String cacheKey) {
        // 记录访问
        recordAccess(cacheKey);
        
        // 先从优化的缓存获取
        String optimizedKey = "optimized:" + cacheKey;
        try {
            var cached = redisCacheClient.get(optimizedKey, new com.fasterxml.jackson.core.type.TypeReference<CursorPageResponse<PostSummaryResponse>>() {});
            if (cached.isPresent()) {
                return cached;
            }
        } catch (Exception e) {
            // 忽略类型转换错误，回退到原始缓存
        }
        
        // 回退到原始缓存
        return delegate.get(cacheKey);
    }

    public void put(String cacheKey, CursorPageResponse<PostSummaryResponse> value, Duration ttl) {
        // 根据访问频率决定缓存策略
        Duration actualTtl = determineTtl(cacheKey, ttl);
        
        // 存储到优化的缓存
        String optimizedKey = "optimized:" + cacheKey;
        redisCacheClient.set(optimizedKey, value, actualTtl);
        
        // 同时存储到原始缓存（兼容性）
        delegate.put(cacheKey, value, ttl);
    }

    public void evictAll() {
        // 智能失效：只失效真正热点的缓存
        evictHotFeedsOnly();
    }

    public void evictPattern(String pattern) {
        // 批量失效特定模式的缓存
        redisCacheClient.evictPattern("optimized:" + pattern);
        redisCacheClient.evictPattern(pattern);
    }

    /**
     * 记录缓存访问
     */
    private void recordAccess(String cacheKey) {
        accessCounters.merge(cacheKey, 1L, Long::sum);
        lastAccessTime.put(cacheKey, System.currentTimeMillis());
    }

    /**
     * 根据访问频率决定TTL
     */
    private Duration determineTtl(String cacheKey, Duration defaultTtl) {
        long accessCount = accessCounters.getOrDefault(cacheKey, 0L);
        
        if (accessCount > HOT_POST_THRESHOLD) {
            return HOT_CACHE_TTL; // 热点数据使用更短的TTL，保证新鲜度
        } else {
            return NORMAL_CACHE_TTL; // 普通数据使用较长的TTL
        }
    }

    /**
     * 只失效热点Feed缓存
     */
    private void evictHotFeedsOnly() {
        accessCounters.entrySet().stream()
                .filter(entry -> entry.getValue() > HOT_POST_THRESHOLD)
                .forEach(entry -> {
                    String cacheKey = entry.getKey();
                    redisCacheClient.evict("optimized:" + cacheKey);
                    delegate.evict(cacheKey);
                });
    }

    /**
     * 批量失效Feed缓存（更温和的策略）
     */
    private void batchEvictHotFeeds() {
        // 只失效访问频率高的缓存
        evictHotFeedsOnly();
        
        // 清理过期的访问计数器
        long currentTime = System.currentTimeMillis();
        lastAccessTime.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > TimeUnit.MINUTES.toMillis(5));
    }

    /**
     * 重置访问计数器
     */
    private void resetCounters() {
        accessCounters.clear();
        // 保留最近访问时间记录
    }

    /**
     * 预热热点缓存
     */
    public void warmupHotFeeds(List<String> cacheKeys) {
        for (String cacheKey : cacheKeys) {
            // 对于预期的热点，提前设置较短的TTL
            if (accessCounters.getOrDefault(cacheKey, 0L) > HOT_POST_THRESHOLD / 2) {
                var cached = delegate.get(cacheKey);
                cached.ifPresent(value -> {
                    String optimizedKey = "optimized:" + cacheKey;
                    redisCacheClient.set(optimizedKey, value, HOT_CACHE_TTL);
                });
            }
        }
    }

    /**
     * 获取缓存统计信息
     */
    public CacheStats getCacheStats() {
        long totalAccesses = accessCounters.values().stream().mapToLong(Long::longValue).sum();
        long hotKeys = accessCounters.values().stream().mapToLong(Long::longValue).filter(count -> count > HOT_POST_THRESHOLD).count();
        
        return new CacheStats(totalAccesses, hotKeys, accessCounters.size());
    }

    /**
     * 缓存统计信息
     */
    public record CacheStats(long totalAccesses, long hotKeys, int totalKeys) {}
}
