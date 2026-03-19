package com.devflow.api.modules.post.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.pagination.CursorPageResponse;
import com.devflow.api.modules.post.dto.response.PostSummaryResponse;
import com.devflow.api.modules.post.service.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserPostController {

    private final PostService postService;

    public UserPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}/posts")
    public ApiResponse<CursorPageResponse<PostSummaryResponse>> getUserPosts(@PathVariable Long id,
                                                                              @RequestParam(required = false) String cursor,
                                                                              @RequestParam(defaultValue = "10") Integer size) {
        return ApiResponse.success(postService.getPostsByAuthor(id, cursor, size == null ? 10 : size));
    }
}
