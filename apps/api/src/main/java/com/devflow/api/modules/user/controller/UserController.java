package com.devflow.api.modules.user.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.UserPrincipalExtractor;
import com.devflow.api.modules.user.dto.request.UpdateProfileRequest;
import com.devflow.api.modules.user.dto.response.UserProfileResponse;
import com.devflow.api.modules.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final UserPrincipalExtractor userPrincipalExtractor;

    public UserController(UserService userService, UserPrincipalExtractor userPrincipalExtractor) {
        this.userService = userService;
        this.userPrincipalExtractor = userPrincipalExtractor;
    }

    @GetMapping("/{id}")
    public ApiResponse<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        return ApiResponse.success(userService.getById(id));
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getMyProfile(Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(userService.getCurrentUser(userId));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateMyProfile(@Valid @RequestBody UpdateProfileRequest request,
                                                            Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(userService.updateCurrentUser(userId, request));
    }
}
