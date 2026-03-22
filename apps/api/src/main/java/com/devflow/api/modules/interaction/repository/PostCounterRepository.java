package com.devflow.api.modules.interaction.repository;

import com.devflow.api.modules.interaction.entity.PostCounterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostCounterRepository extends JpaRepository<PostCounterEntity, Long> {

    Optional<PostCounterEntity> findByPostId(Long postId);

    // 原子性增加点赞数
    @Modifying
    @Query("UPDATE PostCounterEntity pc SET pc.likeCount = pc.likeCount + 1, pc.updatedAt = CURRENT_TIMESTAMP WHERE pc.postId = :postId")
    int incrementLikeCount(@Param("postId") Long postId);

    // 原子性减少点赞数
    @Modifying
    @Query("UPDATE PostCounterEntity pc SET pc.likeCount = GREATEST(pc.likeCount - 1, 0), pc.updatedAt = CURRENT_TIMESTAMP WHERE pc.postId = :postId")
    int decrementLikeCount(@Param("postId") Long postId);

    // 原子性增加评论数
    @Modifying
    @Query("UPDATE PostCounterEntity pc SET pc.commentCount = pc.commentCount + 1, pc.updatedAt = CURRENT_TIMESTAMP WHERE pc.postId = :postId")
    int incrementCommentCount(@Param("postId") Long postId);

    // 原子性减少评论数
    @Modifying
    @Query("UPDATE PostCounterEntity pc SET pc.commentCount = GREATEST(pc.commentCount - 1, 0), pc.updatedAt = CURRENT_TIMESTAMP WHERE pc.postId = :postId")
    int decrementCommentCount(@Param("postId") Long postId);

    // 原子性增加收藏数
    @Modifying
    @Query("UPDATE PostCounterEntity pc SET pc.favoriteCount = pc.favoriteCount + 1, pc.updatedAt = CURRENT_TIMESTAMP WHERE pc.postId = :postId")
    int incrementFavoriteCount(@Param("postId") Long postId);

    // 原子性减少收藏数
    @Modifying
    @Query("UPDATE PostCounterEntity pc SET pc.favoriteCount = GREATEST(pc.favoriteCount - 1, 0), pc.updatedAt = CURRENT_TIMESTAMP WHERE pc.postId = :postId")
    int decrementFavoriteCount(@Param("postId") Long postId);

    // 原子性增加浏览数
    @Modifying
    @Query("UPDATE PostCounterEntity pc SET pc.viewCount = pc.viewCount + 1, pc.updatedAt = CURRENT_TIMESTAMP WHERE pc.postId = :postId")
    int incrementViewCount(@Param("postId") Long postId);

    // 批量获取多个帖子的计数器
    @Query("SELECT pc FROM PostCounterEntity pc WHERE pc.postId IN :postIds")
    List<PostCounterEntity> findByPostIdIn(@Param("postIds") List<Long> postIds);

    // 获取热门帖子计数器
    @Query("SELECT pc FROM PostCounterEntity pc ORDER BY pc.likeCount + pc.commentCount + pc.favoriteCount DESC, pc.updatedAt DESC")
    List<PostCounterEntity> findTopHotPosts(int limit);

    // 检查计数器是否存在
    boolean existsByPostId(Long postId);
}
