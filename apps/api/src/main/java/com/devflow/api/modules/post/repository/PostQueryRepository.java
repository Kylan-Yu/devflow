package com.devflow.api.modules.post.repository;

import com.devflow.api.modules.post.entity.PostEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class PostQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<PostEntity> findLatestPublishedPosts(LocalDateTime cursorPublishedAt,
                                                     Long cursorId,
                                                     Long categoryId,
                                                     int limit) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.*
                FROM posts p
                WHERE p.status = 'PUBLISHED'
                  AND p.visibility = 'PUBLIC'
                  AND p.deleted_at IS NULL
                """);

        if (categoryId != null) {
            sql.append(" AND p.category_id = :categoryId");
        }
        if (cursorPublishedAt != null && cursorId != null) {
            sql.append("""
                     AND (
                       p.published_at < :cursorPublishedAt
                       OR (p.published_at = :cursorPublishedAt AND p.id < :cursorId)
                     )
                    """);
        }

        sql.append(" ORDER BY p.published_at DESC, p.id DESC LIMIT :limit");

        Query query = entityManager.createNativeQuery(sql.toString(), PostEntity.class);
        query.setParameter("limit", limit);
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (cursorPublishedAt != null && cursorId != null) {
            query.setParameter("cursorPublishedAt", cursorPublishedAt);
            query.setParameter("cursorId", cursorId);
        }

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PostEntity> findHotPublishedPosts(Double cursorScore,
                                                  LocalDateTime cursorPublishedAt,
                                                  Long cursorId,
                                                  Long categoryId,
                                                  int limit) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.*
                FROM posts p
                WHERE p.status = 'PUBLISHED'
                  AND p.visibility = 'PUBLIC'
                  AND p.deleted_at IS NULL
                """);

        if (categoryId != null) {
            sql.append(" AND p.category_id = :categoryId");
        }
        if (cursorScore != null && cursorPublishedAt != null && cursorId != null) {
            sql.append("""
                     AND (
                       p.score < :cursorScore
                       OR (p.score = :cursorScore AND p.published_at < :cursorPublishedAt)
                       OR (p.score = :cursorScore AND p.published_at = :cursorPublishedAt AND p.id < :cursorId)
                     )
                    """);
        }

        // EN: Use persisted score to keep query index-friendly on hot path.
        sql.append(" ORDER BY p.score DESC, p.published_at DESC, p.id DESC LIMIT :limit");

        Query query = entityManager.createNativeQuery(sql.toString(), PostEntity.class);
        query.setParameter("limit", limit);
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (cursorScore != null && cursorPublishedAt != null && cursorId != null) {
            query.setParameter("cursorScore", cursorScore);
            query.setParameter("cursorPublishedAt", cursorPublishedAt);
            query.setParameter("cursorId", cursorId);
        }

        return query.getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<PostEntity> findPublishedPostsByAuthor(Long authorId,
                                                        LocalDateTime cursorPublishedAt,
                                                        Long cursorId,
                                                        int limit) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.*
                FROM posts p
                WHERE p.author_id = :authorId
                  AND p.status = 'PUBLISHED'
                  AND p.visibility = 'PUBLIC'
                  AND p.deleted_at IS NULL
                """);

        if (cursorPublishedAt != null && cursorId != null) {
            sql.append("""
                     AND (
                       p.published_at < :cursorPublishedAt
                       OR (p.published_at = :cursorPublishedAt AND p.id < :cursorId)
                     )
                    """);
        }
        sql.append(" ORDER BY p.published_at DESC, p.id DESC LIMIT :limit");

        Query query = entityManager.createNativeQuery(sql.toString(), PostEntity.class);
        query.setParameter("authorId", authorId);
        query.setParameter("limit", limit);
        if (cursorPublishedAt != null && cursorId != null) {
            query.setParameter("cursorPublishedAt", cursorPublishedAt);
            query.setParameter("cursorId", cursorId);
        }
        return query.getResultList();
    }
}
