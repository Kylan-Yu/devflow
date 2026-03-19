package com.devflow.api.common.api;

import com.devflow.api.common.trace.TraceIdHolder;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        String code,
        String message,
        T data,
        String traceId
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ResponseCode.OK.code(), ResponseCode.OK.message(), data, TraceIdHolder.getTraceId());
    }

    public static ApiResponse<Void> success() {
        return success(null);
    }

    public static ApiResponse<Void> failure(ResponseCode responseCode) {
        return new ApiResponse<>(responseCode.code(), responseCode.message(), null, TraceIdHolder.getTraceId());
    }

    public static ApiResponse<Void> failure(ResponseCode responseCode, String message) {
        return new ApiResponse<>(responseCode.code(), message, null, TraceIdHolder.getTraceId());
    }
}
