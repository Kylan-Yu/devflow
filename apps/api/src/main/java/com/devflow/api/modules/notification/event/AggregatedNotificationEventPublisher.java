package com.devflow.api.modules.notification.event;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class AggregatedNotificationEventPublisher {

    private static final int BATCH_SIZE = 100;
    private static final int FLUSH_INTERVAL_SECONDS = 2;
    private static final int MAX_QUEUE_SIZE = 10000;

    private final NotificationEventPublisher delegate;
    private final ScheduledExecutorService scheduler;
    
    // 按用户分组的事件队列
    private final Map<Long, BlockingQueue<InteractionNotificationEvent>> userEventQueues = new ConcurrentHashMap<>();
    
    // 批量处理队列
    private final BlockingQueue<InteractionNotificationEvent> batchQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);

    public AggregatedNotificationEventPublisher(NotificationEventPublisher delegate) {
        this.delegate = delegate;
        this.scheduler = Executors.newScheduledThreadPool(4);
        
        // 定期批量处理事件
        scheduler.scheduleAtFixedRate(this::flushBatchEvents, 
            FLUSH_INTERVAL_SECONDS, FLUSH_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 发布事件（添加到聚合队列）
     */
    public void publishAfterCommit(String routingKey, InteractionNotificationEvent event) {
        try {
            // 添加到批量处理队列
            if (!batchQueue.offer(event)) {
                // 队列满时，直接发布
                delegate.publishAfterCommit(routingKey, event);
            }
        } catch (Exception e) {
            // 出错时直接发布
            delegate.publishAfterCommit(routingKey, event);
        }
    }

    /**
     * 按用户聚合事件
     */
    public void publishUserAggregatedEvent(Long userId, InteractionNotificationEvent event) {
        userEventQueues.computeIfAbsent(userId, k -> new LinkedBlockingQueue<>(1000))
                .offer(event);
    }

    /**
     * 批量处理事件
     */
    private void flushBatchEvents() {
        if (batchQueue.isEmpty()) {
            return;
        }

        List<InteractionNotificationEvent> batch = new ArrayList<>(BATCH_SIZE);
        batchQueue.drainTo(batch, BATCH_SIZE);
        
        if (batch.isEmpty()) {
            return;
        }

        // 按类型和接收者分组
        Map<String, List<InteractionNotificationEvent>> groupedEvents = batch.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                    event -> event.receiverId() + ":" + event.eventType()));

        // 批量发布
        for (Map.Entry<String, List<InteractionNotificationEvent>> entry : groupedEvents.entrySet()) {
            List<InteractionNotificationEvent> events = entry.getValue();
            
            // 如果是同一用户的同一类型事件，只发送最新的
            if (events.size() > 1) {
                InteractionNotificationEvent latestEvent = events.get(events.size() - 1);
                String[] parts = entry.getKey().split(":");
                String routingKey = getRoutingKey(latestEvent.eventType());
                delegate.publishAfterCommit(routingKey, latestEvent);
            } else {
                InteractionNotificationEvent singleEvent = events.get(0);
                String routingKey = getRoutingKey(singleEvent.eventType());
                delegate.publishAfterCommit(routingKey, singleEvent);
            }
        }
    }

    /**
     * 处理用户特定的事件聚合
     */
    private void flushUserEvents() {
        for (Map.Entry<Long, BlockingQueue<InteractionNotificationEvent>> entry : userEventQueues.entrySet()) {
            Long userId = entry.getKey();
            BlockingQueue<InteractionNotificationEvent> queue = entry.getValue();
            
            List<InteractionNotificationEvent> events = new ArrayList<>(50);
            queue.drainTo(events, 50);
            
            if (events.isEmpty()) {
                continue;
            }
            
            // 只发送最新的事件，避免重复通知
            InteractionNotificationEvent latestEvent = events.get(events.size() - 1);
            String routingKey = getRoutingKey(latestEvent.eventType());
            delegate.publishAfterCommit(routingKey, latestEvent);
        }
    }

    /**
     * 获取路由键
     */
    private String getRoutingKey(InteractionEventType eventType) {
        return switch (eventType) {
            case POST_LIKED -> NotificationEventRouting.ROUTING_KEY_LIKE;
            case POST_COMMENTED -> NotificationEventRouting.ROUTING_KEY_COMMENT;
            case USER_FOLLOWED -> NotificationEventRouting.ROUTING_KEY_FOLLOW;
            default -> "interaction.default";
        };
    }

    /**
     * 获取队列统计信息
     */
    public QueueStats getQueueStats() {
        return new QueueStats(
            batchQueue.size(),
            userEventQueues.values().stream().mapToInt(BlockingQueue::size).sum(),
            userEventQueues.size()
        );
    }

    /**
     * 清理过期的用户队列
     */
    public void cleanupExpiredQueues() {
        userEventQueues.entrySet().removeIf(entry -> {
            BlockingQueue<InteractionNotificationEvent> queue = entry.getValue();
            return queue.isEmpty();
        });
    }

    /**
     * 队列统计信息
     */
    public record QueueStats(int batchQueueSize, int totalUserEvents, int activeUserQueues) {}
}
