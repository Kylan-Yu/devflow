package com.devflow.api.modules.interaction.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.UserPrincipalExtractor;
import com.devflow.api.modules.interaction.dto.response.FollowStatusResponse;
import com.devflow.api.modules.interaction.service.InteractionService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class FollowController {

    private final InteractionService interactionService;
    private final UserPrincipalExtractor userPrincipalExtractor;

    public FollowController(InteractionService interactionService,
                            UserPrincipalExtractor userPrincipalExtractor) {
        this.interactionService = interactionService;
        this.userPrincipalExtractor = userPrincipalExtractor;
    }

    @PostMapping("/{id}/follow")
    public ApiResponse<FollowStatusResponse> follow(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(interactionService.follow(userId, id));
    }

    @DeleteMapping("/{id}/follow")
    public ApiResponse<FollowStatusResponse> unfollow(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(interactionService.unfollow(userId, id));
    }

    @GetMapping("/{id}/follow-status")
    public ApiResponse<FollowStatusResponse> followStatus(@PathVariable Long id, Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(interactionService.followStatus(userId, id));
    }
}
