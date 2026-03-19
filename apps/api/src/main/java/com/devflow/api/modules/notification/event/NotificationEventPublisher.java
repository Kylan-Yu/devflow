package com.devflow.api.modules.notification.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Component
public class NotificationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public NotificationEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /*
     * EN: Publish after transaction commit to avoid notifying on rolled-back writes.
     * 中文：在事务提交后发布事件，避免业务回滚后仍然下发通知。
     */
    public void publishAfterCommit(String routingKey, InteractionNotificationEvent event) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publishNow(routingKey, event);
                }
            });
            return;
        }
        publishNow(routingKey, event);
    }

    private void publishNow(String routingKey, InteractionNotificationEvent event) {
        rabbitTemplate.convertAndSend(NotificationEventRouting.EXCHANGE, routingKey, event);
    }
}
