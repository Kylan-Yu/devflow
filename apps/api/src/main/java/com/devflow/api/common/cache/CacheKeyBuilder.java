package com.devflow.api.common.cache;

public final class CacheKeyBuilder {

    public static final String PREFIX = "devflow:cache:";

    private CacheKeyBuilder() {
    }

    public static String feedFirstPage(String feedType, int size, Long categoryId) {
        return PREFIX + "feed:" + feedType + ":size:" + size + ":category:" + (categoryId == null ? "all" : categoryId);
    }

    public static String postDetail(Long postId) {
        return PREFIX + "post:detail:" + postId;
    }

    public static String notificationUnread(Long userId) {
        return PREFIX + "notification:unread:" + userId;
    }

    public static String userProfile(Long userId) {
        return PREFIX + "user:profile:" + userId;
    }

    public static String postCounter(Long postId) {
        return PREFIX + "post:counter:" + postId;
    }
}
