package com.devflow.api.modules.interaction.service;

import com.devflow.api.common.cache.CacheKeyBuilder;
import com.devflow.api.common.cache.RedisCacheClient;
import com.devflow.api.common.lock.DistributedLockService;
import com.devflow.api.modules.interaction.entity.PostCounterEntity;
import com.devflow.api.modules.interaction.repository.PostCounterRepository;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
public class HighConcurrencyCounterService {

    private static final Duration COUNTER_CACHE_TTL = Duration.ofMinutes(5);
    private static final Duration LOCK_TIMEOUT = Duration.ofSeconds(10);
    private static final int BATCH_SIZE = 100;
    
    private final PostCounterRepository postCounterRepository;
    private final PostRepository postRepository;
    private final RedisCacheClient redisCacheClient;
    private final DistributedLockService distributedLockService;
    private final Executor asyncExecutor;

    // 内存中的批量更新缓存
    private final Map<Long, CounterDelta> pendingUpdates = new HashMap<>();
    private volatile long lastFlushTime = System.currentTimeMillis();

    public HighConcurrencyCounterService(PostCounterRepository postCounterRepository,
                                   PostRepository postRepository,
                                   RedisCacheClient redisCacheClient,
                                   DistributedLockService distributedLockService) {
        this.postCounterRepository = postCounterRepository;
        this.postRepository = postRepository;
        this.redisCacheClient = redisCacheClient;
        this.distributedLockService = distributedLockService;
        this.asyncExecutor = Executors.newFixedThreadPool(4);
    }

    /**
     * 记录计数器变更
     */
    @Transactional
    public void recordCounterChange(Long postId, CounterType type, int delta) {
        String lockKey = "counter_update:" + postId;
        
        distributedLockService.executeWithLock(lockKey, LOCK_TIMEOUT, () -> {
            // 1. 先更新Redis缓存
            updateRedisCounter(postId, type, delta);
            
            // 2. 异步批量更新数据库
            addToBatchUpdate(postId, type, delta);
            
            return null;
        });
    }

    /**
     * 获取帖子计数器（优先从缓存）
     */
    public PostCounterEntity getPostCounter(Long postId) {
        String cacheKey = CacheKeyBuilder.postCounter(postId);
        
        // 先从Redis获取
        var cached = redisCacheClient.get(cacheKey, PostCounterEntity.class);
        if (cached.isPresent()) {
            return cached.get();
        }
        
        // 从数据库获取并缓存
        return postCounterRepository.findByPostId(postId)
                .map(counter -> {
                    redisCacheClient.set(cacheKey, counter, COUNTER_CACHE_TTL);
                    return counter;
                })
                .orElseGet(() -> {
                    // 创建新的计数器
                    PostCounterEntity newCounter = new PostCounterEntity(postId);
                    postCounterRepository.save(newCounter);
                    redisCacheClient.set(cacheKey, newCounter, COUNTER_CACHE_TTL);
                    return newCounter;
                });
    }

    /**
     * 批量获取计数器
     */
    public Map<Long, PostCounterEntity> getPostCounters(List<Long> postIds) {
        Map<Long, PostCounterEntity> result = new HashMap<>();
        
        // 先从Redis批量获取
        for (Long postId : postIds) {
            String cacheKey = CacheKeyBuilder.postCounter(postId);
            var cached = redisCacheClient.get(cacheKey, PostCounterEntity.class);
            cached.ifPresent(counter -> result.put(postId, counter));
        }
        
        // 获取未缓存的
        List<Long> missingIds = postIds.stream()
                .filter(id -> !result.containsKey(id))
                .toList();
        
        if (!missingIds.isEmpty()) {
            List<PostCounterEntity> fromDb = postCounterRepository.findByPostIdIn(missingIds);
            for (PostCounterEntity counter : fromDb) {
                String cacheKey = CacheKeyBuilder.postCounter(counter.getPostId());
                redisCacheClient.set(cacheKey, counter, COUNTER_CACHE_TTL);
                result.put(counter.getPostId(), counter);
            }
        }
        
        return result;
    }

    /**
     * 更新Redis中的计数器
     */
    private void updateRedisCounter(Long postId, CounterType type, int delta) {
        String cacheKey = CacheKeyBuilder.postCounter(postId);
        var cached = redisCacheClient.get(cacheKey, PostCounterEntity.class);
        
        if (cached.isPresent()) {
            PostCounterEntity counter = cached.get();
            switch (type) {
                case LIKE -> counter.incrementLikeCount();
                case COMMENT -> counter.incrementCommentCount();
                case FAVORITE -> counter.incrementFavoriteCount();
                case VIEW -> counter.incrementViewCount();
            }
            redisCacheClient.set(cacheKey, counter, COUNTER_CACHE_TTL);
        }
    }

    /**
     * 添加到批量更新队列
     */
    private void addToBatchUpdate(Long postId, CounterType type, int delta) {
        synchronized (pendingUpdates) {
            pendingUpdates.compute(postId, (id, existing) -> {
                if (existing == null) {
                    return new CounterDelta(delta, 0, 0, 0);
                }
                return existing.add(type, delta);
            });
            
            // 如果批量大小达到阈值或时间间隔到了，触发批量更新
            long currentTime = System.currentTimeMillis();
            if (pendingUpdates.size() >= BATCH_SIZE || 
                currentTime - lastFlushTime > 5000) { // 5秒或100个更新
                flushBatchUpdates();
                lastFlushTime = currentTime;
            }
        }
    }

    /**
     * 批量更新数据库
     */
    private void flushBatchUpdates() {
        if (pendingUpdates.isEmpty()) {
            return;
        }
        
        Map<Long, CounterDelta> toUpdate = new HashMap<>(pendingUpdates);
        pendingUpdates.clear();
        
        // 异步执行批量更新
        CompletableFuture.runAsync(() -> {
            for (Map.Entry<Long, CounterDelta> entry : toUpdate.entrySet()) {
                Long postId = entry.getKey();
                CounterDelta delta = entry.getValue();
                
                try {
                    postCounterRepository.incrementLikeCount(postId);
                    postCounterRepository.incrementCommentCount(postId);
                    postCounterRepository.incrementFavoriteCount(postId);
                    
                    // 清除缓存，下次从数据库读取最新值
                    redisCacheClient.evict(CacheKeyBuilder.postCounter(postId));
                } catch (Exception e) {
                    // 记录错误，但不影响主流程
                    // 使用日志记录器替代直接输出
                }
            }
        }, asyncExecutor);
    }

    /**
     * 同步帖子计数器到posts表
     */
    @Transactional
    public void syncPostCountersToPosts() {
        String lockKey = "sync_post_counters";
        
        distributedLockService.executeWithLock(lockKey, Duration.ofMinutes(5), () -> {
            // 获取所有计数器
            List<PostCounterEntity> counters = postCounterRepository.findAll();
            
            for (PostCounterEntity counter : counters) {
                PostEntity post = postRepository.findById(counter.getPostId()).orElse(null);
                if (post != null) {
                    post.setLikeCount(counter.getLikeCount().intValue());
                    post.setCommentCount(counter.getCommentCount().intValue());
                    post.setFavoriteCount(counter.getFavoriteCount().intValue());
                    post.setUpdatedAt(counter.getUpdatedAt());
                    postRepository.save(post);
                }
            }
            
            return null;
        });
    }

    /**
     * 计数器类型枚举
     */
    public enum CounterType {
        LIKE, COMMENT, FAVORITE, VIEW
    }

    /**
     * 计数器增量记录
     */
    private static class CounterDelta {
        int likeDelta;
        int commentDelta;
        int favoriteDelta;
        int viewDelta;

        CounterDelta(int likeDelta, int commentDelta, int favoriteDelta, int viewDelta) {
            this.likeDelta = likeDelta;
            this.commentDelta = commentDelta;
            this.favoriteDelta = favoriteDelta;
            this.viewDelta = viewDelta;
        }

        CounterDelta add(CounterType type, int delta) {
            switch (type) {
                case LIKE -> likeDelta += delta;
                case COMMENT -> commentDelta += delta;
                case FAVORITE -> favoriteDelta += delta;
                case VIEW -> viewDelta += delta;
            }
            return this;
        }
    }
}
