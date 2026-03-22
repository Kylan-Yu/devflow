package com.devflow.api.modules.interaction.service;

import com.devflow.api.modules.interaction.entity.PostCounterEntity;
import com.devflow.api.modules.interaction.repository.PostCounterRepository;
import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.repository.PostRepository;
import com.devflow.api.common.cache.RedisCacheClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SimpleCounterService {

    private static final Duration COUNTER_CACHE_TTL = Duration.ofMinutes(5);
    
    private final PostCounterRepository postCounterRepository;
    private final PostRepository postRepository;
    private final RedisCacheClient redisCacheClient;

    public SimpleCounterService(PostCounterRepository postCounterRepository,
                             PostRepository postRepository,
                             RedisCacheClient redisCacheClient) {
        this.postCounterRepository = postCounterRepository;
        this.postRepository = postRepository;
        this.redisCacheClient = redisCacheClient;
    }

    /**
     * 记录计数器变更 - 使用批量更新方法
     */
    @Transactional
    public void recordCounterChange(Long postId, CounterType type, int delta) {
        System.out.println("DEBUG: 计数器变更 - postId: " + postId + ", type: " + type + ", delta: " + delta);
        
        // 使用批量更新方法，确保原子性
        try {
            switch (type) {
                case LIKE -> {
                    postCounterRepository.batchIncrementLikeCount(postId, delta);
                    System.out.println("DEBUG: 批量更新点赞数 delta: " + delta + " - postId: " + postId);
                    break;
                }
                case COMMENT -> {
                    postCounterRepository.batchIncrementCommentCount(postId, delta);
                    System.out.println("DEBUG: 批量更新评论数 delta: " + delta + " - postId: " + postId);
                    break;
                }
                case FAVORITE -> {
                    postCounterRepository.batchIncrementFavoriteCount(postId, delta);
                    System.out.println("DEBUG: 批量更新收藏数 delta: " + delta + " - postId: " + postId);
                    break;
                }
                case VIEW -> {
                    if (delta > 0) {
                        postCounterRepository.batchIncrementViewCount(postId, delta);
                        System.out.println("DEBUG: 批量更新浏览数 delta: " + delta + " - postId: " + postId);
                    }
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("DEBUG: 数据库更新异常 - " + e.getMessage());
        }
        
        // 无缓存操作
        System.out.println("DEBUG: 计数器变更完成，批量更新");
    }

    /**
     * 获取帖子计数器（直接从数据库获取，临时禁用缓存）
     */
    public PostCounterEntity getPostCounter(Long postId) {
        System.out.println("DEBUG: getPostCounter调用 - postId: " + postId);
        
        // 临时禁用Redis缓存，直接从数据库获取
        PostCounterEntity counter = postCounterRepository.findByPostId(postId)
                .orElseGet(() -> {
                    // 创建新的计数器
                    PostCounterEntity newCounter = new PostCounterEntity(postId);
                    postCounterRepository.save(newCounter);
                    System.out.println("DEBUG: 创建新计数器 - postId: " + postId + ", likeCount: " + newCounter.getLikeCount());
                    return newCounter;
                });
        
        System.out.println("DEBUG: 数据库中的实际值 - postId: " + postId + 
            ", likeCount: " + counter.getLikeCount() + 
            ", commentCount: " + counter.getCommentCount() + 
            ", favoriteCount: " + counter.getFavoriteCount());
        
        return counter;
    }

    /**
     * 计数器类型枚举
     */
    public enum CounterType {
        LIKE, COMMENT, FAVORITE, VIEW
    }

    /**
     * 更新Redis中的计数器
     */
    private void updateRedisCounter(Long postId, CounterType type, int delta) {
        String cacheKey = "counter:post:" + postId;
        var cached = redisCacheClient.get(cacheKey, PostCounterEntity.class);
        
        if (cached.isPresent()) {
            PostCounterEntity counter = cached.get();
            switch (type) {
                case LIKE -> {
                    if (delta > 0) {
                        counter.incrementLikeCount();
                    } else {
                        counter.decrementLikeCount();
                    }
                    break;
                }
                case COMMENT -> {
                    if (delta > 0) {
                        counter.incrementCommentCount();
                    } else {
                        counter.decrementCommentCount();
                    }
                    break;
                }
                case FAVORITE -> {
                    if (delta > 0) {
                        counter.incrementFavoriteCount();
                    } else {
                        counter.decrementFavoriteCount();
                    }
                    break;
                }
                case VIEW -> {
                    if (delta > 0) {
                        counter.incrementViewCount();
                    }
                    break;
                }
            }
            redisCacheClient.set(cacheKey, counter, COUNTER_CACHE_TTL);
        }
    }
}
