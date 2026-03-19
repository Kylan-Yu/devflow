package com.devflow.api.modules.post.service;

import com.devflow.api.modules.post.dto.response.CategoryResponse;
import com.devflow.api.modules.post.dto.response.TagResponse;
import com.devflow.api.modules.post.entity.CategoryStatus;
import com.devflow.api.modules.post.entity.TagStatus;
import com.devflow.api.modules.post.repository.CategoryRepository;
import com.devflow.api.modules.post.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostCatalogService {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public PostCatalogService(CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findByStatusOrderBySortOrderAscIdAsc(CategoryStatus.ACTIVE)
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TagResponse> listTags(String keyword, int size) {
        int normalizedSize = Math.max(1, Math.min(size, 50));
        return tagRepository.searchByStatusAndKeyword(TagStatus.ACTIVE, keyword == null ? null : keyword.trim())
                .stream()
                .limit(normalizedSize)
                .map(TagResponse::from)
                .toList();
    }
}
