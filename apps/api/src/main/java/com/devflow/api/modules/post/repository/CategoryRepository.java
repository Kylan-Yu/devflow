package com.devflow.api.modules.post.repository;

import com.devflow.api.modules.post.entity.CategoryEntity;
import com.devflow.api.modules.post.entity.CategoryStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    boolean existsByIdAndStatus(Long id, CategoryStatus status);

    List<CategoryEntity> findByStatusOrderBySortOrderAscIdAsc(CategoryStatus status);
}
