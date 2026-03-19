package com.devflow.api.modules.notification.event;

public final class NotificationEventRouting {

    public static final String EXCHANGE = "devflow.interaction.events";
    public static final String QUEUE = "devflow.notification.events.queue";
    public static final String ROUTING_KEY_LIKE = "interaction.like.created";
    public static final String ROUTING_KEY_COMMENT = "interaction.comment.created";
    public static final String ROUTING_KEY_FOLLOW = "interaction.follow.created";

    private NotificationEventRouting() {
    }
}
