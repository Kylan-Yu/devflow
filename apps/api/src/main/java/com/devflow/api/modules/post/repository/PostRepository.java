package com.devflow.api.modules.post.repository;

import com.devflow.api.modules.post.entity.PostEntity;
import com.devflow.api.modules.post.entity.PostStatus;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    Optional<PostEntity> findByIdAndDeletedAtIsNull(Long id);

    Optional<PostEntity> findByIdAndStatusAndDeletedAtIsNull(Long id, PostStatus status);

    List<PostEntity> findByIdIn(Collection<Long> ids);

    Page<PostEntity> findAll(Pageable pageable);

    Page<PostEntity> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content, Pageable pageable);

    long countByStatus(PostStatus status);
}
