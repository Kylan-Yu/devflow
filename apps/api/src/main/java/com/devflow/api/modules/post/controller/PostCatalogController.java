package com.devflow.api.modules.post.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.modules.post.dto.response.CategoryResponse;
import com.devflow.api.modules.post.dto.response.TagResponse;
import com.devflow.api.modules.post.service.PostCatalogService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PostCatalogController {

    private final PostCatalogService postCatalogService;

    public PostCatalogController(PostCatalogService postCatalogService) {
        this.postCatalogService = postCatalogService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> listCategories() {
        return ApiResponse.success(postCatalogService.listCategories());
    }

    @GetMapping("/tags")
    public ApiResponse<List<TagResponse>> listTags(@RequestParam(required = false) String keyword,
                                                   @RequestParam(defaultValue = "20") Integer size) {
        return ApiResponse.success(postCatalogService.listTags(keyword, size == null ? 20 : size));
    }
}
