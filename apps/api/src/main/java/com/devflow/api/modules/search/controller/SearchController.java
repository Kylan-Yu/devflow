package com.devflow.api.modules.search.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import com.devflow.api.modules.search.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/posts")
    public ApiResponse<CursorPageResponse<PostSummaryResponse>> searchPosts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return ApiResponse.success(searchService.searchPosts(keyword, categoryId, cursor, size == null ? 10 : size));
    }
}
