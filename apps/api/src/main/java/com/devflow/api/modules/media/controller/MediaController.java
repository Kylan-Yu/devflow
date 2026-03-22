package com.devflow.api.modules.media.controller;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.security.UserPrincipalExtractor;
import com.devflow.api.modules.media.dto.response.UploadedMediaResponse;
import com.devflow.api.modules.media.service.MediaStorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/media")
@ConditionalOnProperty(name = "devflow.storage.minio.enabled", havingValue = "true")
public class MediaController {

    private final MediaStorageService mediaStorageService;
    private final UserPrincipalExtractor userPrincipalExtractor;

    public MediaController(MediaStorageService mediaStorageService,
                           UserPrincipalExtractor userPrincipalExtractor) {
        this.mediaStorageService = mediaStorageService;
        this.userPrincipalExtractor = userPrincipalExtractor;
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UploadedMediaResponse> uploadAvatar(@RequestPart("file") MultipartFile file,
                                                           Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(mediaStorageService.uploadAvatar(userId, file));
    }

    @PostMapping(value = "/post-cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UploadedMediaResponse> uploadPostCover(@RequestPart("file") MultipartFile file,
                                                              Authentication authentication) {
        Long userId = userPrincipalExtractor.requireUserId(authentication);
        return ApiResponse.success(mediaStorageService.uploadPostCover(userId, file));
    }
}
