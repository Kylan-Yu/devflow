package com.devflow.api.modules.post.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.UserPrincipalExtractor;
import com.devflow.api.modules.post.dto.request.CreatePostRequest;
import com.devflow.api.modules.post.dto.request.UpdatePostRequest;
import com.devflow.api.modules.post.dto.response.PostDetailResponse;
import com.devflow.api.modules.post.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;
    private final UserPrincipalExtractor userPrincipalExtractor;

    public PostController(PostService postService, UserPrincipalExtractor userPrincipalExtractor) {
        this.postService = postService;
        this.userPrincipalExtractor = userPrincipalExtractor;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PostDetailResponse>> createPost(@Valid @RequestBody CreatePostRequest request,
                                                                      Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        PostDetailResponse response = postService.createPost(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ApiResponse<PostDetailResponse> updatePost(@PathVariable Long id,
                                                      @Valid @RequestBody UpdatePostRequest request,
                                                      Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(postService.updatePost(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        postService.deletePost(userId, id);
        return ApiResponse.success();
    }

    @GetMapping("/{id}")
    public ApiResponse<PostDetailResponse> getPostDetail(@PathVariable Long id) {
        return ApiResponse.success(postService.getPostDetail(id));
    }
}
