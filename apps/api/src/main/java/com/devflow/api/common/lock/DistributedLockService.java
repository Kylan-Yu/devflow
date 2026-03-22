package com.devflow.api.common.lock;

import com.devflow.api.common.cache.RedisCacheClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

@Service
public class DistributedLockService {

    private final RedisCacheClient redisCacheClient;
    private static final Duration DEFAULT_LOCK_TIMEOUT = Duration.ofSeconds(30);
    private static final Duration LOCK_RETRY_INTERVAL = Duration.ofMillis(50);

    public DistributedLockService(RedisCacheClient redisCacheClient) {
        this.redisCacheClient = redisCacheClient;
    }

    /**
     * 执行带分布式锁的操作
     * @param lockKey 锁的key
     * @param timeout 锁超时时间
     * @param operation 要执行的操作
     * @return 操作结果
     */
    public <T> T executeWithLock(String lockKey, Duration timeout, Supplier<T> operation) {
        String lockValue = UUID.randomUUID().toString();
        String fullLockKey = "lock:" + lockKey;
        
        try {
            if (acquireLock(fullLockKey, lockValue, timeout)) {
                return operation.get();
            } else {
                throw new RuntimeException("Failed to acquire lock: " + lockKey);
            }
        } finally {
            releaseLock(fullLockKey, lockValue);
        }
    }

    /**
     * 尝试获取分布式锁
     */
    private boolean acquireLock(String lockKey, String lockValue, Duration timeout) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + timeout.toMillis();
        
        while (System.currentTimeMillis() < endTime) {
            // 使用SET NX EX原子性设置锁
            Boolean acquired = redisCacheClient.setIfAbsent(lockKey, lockValue, timeout);
            if (Boolean.TRUE.equals(acquired)) {
                return true;
            }
            
            try {
                Thread.sleep(LOCK_RETRY_INTERVAL.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        
        return false;
    }

    /**
     * 释放分布式锁
     */
    private void releaseLock(String lockKey, String lockValue) {
        // Lua脚本确保原子性释放锁
        String luaScript = """
            if redis.call("get", KEYS[1]) == ARGV[1] then
                return redis.call("del", KEYS[1])
            else
                return 0
            end
        """;
        
        redisCacheClient.eval(luaScript, 1, lockKey, lockValue);
    }

    /**
     * 简化的锁执行方法，使用默认超时时间
     */
    public <T> T executeWithLock(String lockKey, Supplier<T> operation) {
        return executeWithLock(lockKey, DEFAULT_LOCK_TIMEOUT, operation);
    }

    /**
     * 尝试执行带锁的操作，失败时返回默认值
     */
    public <T> T tryWithLock(String lockKey, Supplier<T> operation, T defaultValue) {
        try {
            return executeWithLock(lockKey, Duration.ofSeconds(5), operation);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
