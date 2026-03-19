package com.devflow.api.modules.post.repository;

import com.devflow.api.modules.post.entity.TagEntity;
import com.devflow.api.modules.post.entity.TagStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<TagEntity, Long> {

    @Query("""
            SELECT t
            FROM TagEntity t
            WHERE t.status = :status
              AND (:keyword IS NULL OR :keyword = '' OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY t.name ASC
            """)
    List<TagEntity> searchByStatusAndKeyword(@Param("status") TagStatus status, @Param("keyword") String keyword);
}
