package com.devflow.api.modules.media.dto.response;

public record UploadedMediaResponse(
        String url,
        String objectKey,
        String contentType,
        long size
) {
}
