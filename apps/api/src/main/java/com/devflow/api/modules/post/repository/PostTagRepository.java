package com.devflow.api.modules.post.repository;

import com.devflow.api.modules.post.entity.PostTagEntity;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTagEntity, Long> {

    void deleteByPostId(Long postId);

    List<PostTagEntity> findByPostIdIn(Collection<Long> postIds);
}
