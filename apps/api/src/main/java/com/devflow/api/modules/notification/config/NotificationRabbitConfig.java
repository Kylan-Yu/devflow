package com.devflow.api.modules.notification.config;

import com.devflow.api.modules.notification.event.NotificationEventRouting;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationRabbitConfig {

    @Bean
    public TopicExchange interactionEventExchange() {
        return new TopicExchange(NotificationEventRouting.EXCHANGE, true, false);
    }

    @Bean
    public Queue notificationEventQueue() {
        return new Queue(NotificationEventRouting.QUEUE, true);
    }

    @Bean
    public Binding likeCreatedBinding(Queue notificationEventQueue, TopicExchange interactionEventExchange) {
        return BindingBuilder.bind(notificationEventQueue)
                .to(interactionEventExchange)
                .with(NotificationEventRouting.ROUTING_KEY_LIKE);
    }

    @Bean
    public Binding commentCreatedBinding(Queue notificationEventQueue, TopicExchange interactionEventExchange) {
        return BindingBuilder.bind(notificationEventQueue)
                .to(interactionEventExchange)
                .with(NotificationEventRouting.ROUTING_KEY_COMMENT);
    }

    @Bean
    public Binding followCreatedBinding(Queue notificationEventQueue, TopicExchange interactionEventExchange) {
        return BindingBuilder.bind(notificationEventQueue)
                .to(interactionEventExchange)
                .with(NotificationEventRouting.ROUTING_KEY_FOLLOW);
    }

    @Bean
    public MessageConverter rabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
