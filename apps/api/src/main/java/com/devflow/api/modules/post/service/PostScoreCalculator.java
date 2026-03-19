package com.devflow.api.modules.post.service;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class PostScoreCalculator {

    public double calculateHotScore(int likeCount, int commentCount, int favoriteCount, LocalDateTime publishedAt) {
        double hoursSincePublished = Math.max(0, Duration.between(publishedAt, LocalDateTime.now()).toMinutes() / 60.0);
        double engagementScore = (3.0 * likeCount) + (4.0 * commentCount) + (2.0 * favoriteCount) + 1.0;
        double decay = Math.pow(hoursSincePublished + 2.0, 1.3);
        return Math.round((engagementScore / decay) * 1_000_000d) / 1_000_000d;
    }
}
