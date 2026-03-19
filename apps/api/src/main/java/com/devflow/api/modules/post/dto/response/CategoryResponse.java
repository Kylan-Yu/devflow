package com.devflow.api.modules.post.dto.response;

import com.devflow.api.modules.post.entity.CategoryEntity;

public record CategoryResponse(
        Long id,
        String code,
        String nameZh,
        String nameEn,
        Integer sortOrder
) {
    public static CategoryResponse from(CategoryEntity category) {
        return new CategoryResponse(
                category.getId(),
                category.getCode(),
                category.getNameZh(),
                category.getNameEn(),
                category.getSortOrder()
        );
    }
}
