package com.devflow.api.modules.interaction.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_counters")
public class PostCounterEntity {

    @Id
    private Long postId;

    @Column(name = "like_count", nullable = false)
    private Long likeCount = 0L;

    @Column(name = "comment_count", nullable = false)
    private Long commentCount = 0L;

    @Column(name = "favorite_count", nullable = false)
    private Long favoriteCount = 0L;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    // Constructors
    public PostCounterEntity() {}

    public PostCounterEntity(Long postId) {
        this.postId = postId;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Long getViewCount() {
        return viewCount;
    }

    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    // Utility methods
    public void incrementLikeCount() {
        this.likeCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decrementLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementCommentCount() {
        this.commentCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decrementCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementFavoriteCount() {
        this.favoriteCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void decrementFavoriteCount() {
        this.favoriteCount = Math.max(0, this.favoriteCount - 1);
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementViewCount() {
        this.viewCount++;
        this.updatedAt = LocalDateTime.now();
    }
}
