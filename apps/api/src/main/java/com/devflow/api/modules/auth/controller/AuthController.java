package com.devflow.api.modules.auth.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.modules.auth.dto.request.LoginRequest;
import com.devflow.api.modules.auth.dto.request.LogoutRequest;
import com.devflow.api.modules.auth.dto.request.RefreshTokenRequest;
import com.devflow.api.modules.auth.dto.request.RegisterRequest;
import com.devflow.api.modules.auth.dto.response.AuthSessionResponse;
import com.devflow.api.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthSessionResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthSessionResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ApiResponse<AuthSessionResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthSessionResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refresh(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ApiResponse.success();
    }
}
