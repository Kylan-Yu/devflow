package com.devflow.api.modules.interaction.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.UserPrincipalExtractor;
import com.devflow.api.modules.interaction.dto.request.CreateCommentRequest;
import com.devflow.api.modules.interaction.dto.response.CommentResponse;
import com.devflow.api.modules.interaction.dto.response.PostInteractionStateResponse;
import com.devflow.api.modules.interaction.dto.response.PostInteractionSummaryResponse;
import com.devflow.api.modules.interaction.service.InteractionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
public class PostInteractionController {

    private final InteractionService interactionService;
    private final UserPrincipalExtractor userPrincipalExtractor;

    public PostInteractionController(InteractionService interactionService,
                                     UserPrincipalExtractor userPrincipalExtractor) {
        this.interactionService = interactionService;
        this.userPrincipalExtractor = userPrincipalExtractor;
    }

    @PostMapping("/{id}/likes")
    public ApiResponse<PostInteractionSummaryResponse> like(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(interactionService.likePost(userId, id));
    }

    @DeleteMapping("/{id}/likes")
    public ApiResponse<PostInteractionSummaryResponse> unlike(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(interactionService.unlikePost(userId, id));
    }

    @PostMapping("/{id}/favorites")
    public ApiResponse<PostInteractionSummaryResponse> favorite(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(interactionService.favoritePost(userId, id));
    }

    @DeleteMapping("/{id}/favorites")
    public ApiResponse<PostInteractionSummaryResponse> unfavorite(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(interactionService.unfavoritePost(userId, id));
    }

    @GetMapping("/{id}/interaction-status")
    public ApiResponse<PostInteractionStateResponse> interactionState(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(interactionService.postInteractionState(userId, id));
    }

    @GetMapping("/{id}/comments")
    public ApiResponse<List<CommentResponse>> comments(@PathVariable Long id) {
        return ApiResponse.success(interactionService.listComments(id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(@PathVariable Long id,
                                                                      @Valid @RequestBody CreateCommentRequest request,
                                                                      Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        CommentResponse response = interactionService.createComment(userId, id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }
}
