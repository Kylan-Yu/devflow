package com.devflow.api.modules.interaction.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.UserPrincipalExtractor;
import com.devflow.api.modules.interaction.dto.response.PostInteractionSummaryResponse;
import com.devflow.api.modules.interaction.service.InteractionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
public class CommentInteractionController {

    private final InteractionService interactionService;
    private final UserPrincipalExtractor userPrincipalExtractor;

    public CommentInteractionController(InteractionService interactionService,
                                        UserPrincipalExtractor userPrincipalExtractor) {
        this.interactionService = interactionService;
        this.userPrincipalExtractor = userPrincipalExtractor;
    }

    @DeleteMapping("/{id}")
    public ApiResponse<PostInteractionSummaryResponse> deleteComment(@PathVariable Long id,
                                                                     Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(interactionService.deleteComment(userId, id));
    }
}
