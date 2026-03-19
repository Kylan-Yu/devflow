package com.devflow.api.modules.post.dto.response;

import com.devflow.api.modules.post.entity.TagEntity;

public record TagResponse(
        Long id,
        String name,
        String localeKey
) {
    public static TagResponse from(TagEntity tag) {
        return new TagResponse(tag.getId(), tag.getName(), tag.getLocaleKey());
    }
}
