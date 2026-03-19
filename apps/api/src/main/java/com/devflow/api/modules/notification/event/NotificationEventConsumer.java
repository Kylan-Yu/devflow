package com.devflow.api.modules.notification.event;

import com.devflow.api.modules.notification.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventConsumer {

    private final NotificationService notificationService;

    public NotificationEventConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /*
     * EN: Interaction events are consumed asynchronously here and transformed into
     * notification records + websocket pushes.
     * 中文：互动事件在这里异步消费，并转化为通知落库与 WebSocket 推送。
     */
    @RabbitListener(queues = NotificationEventRouting.QUEUE)
    public void onInteractionEvent(InteractionNotificationEvent event) {
        notificationService.handleInteractionEvent(event);
    }
}
