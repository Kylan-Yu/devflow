package com.devflow.api.common.exception;

import com.devflow.api.common.api.ApiResponse;
import com.devflow.api.common.api.ResponseCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ResponseCode responseCode = exception.getResponseCode();
        return ResponseEntity.status(responseCode.httpStatus())
                .body(ApiResponse.failure(responseCode, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException exception) {
        FieldError firstError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = firstError == null
                ? ResponseCode.VALIDATION_ERROR.message()
                : ResponseCode.VALIDATION_ERROR.message() + ":" + firstError.getField();
        return ResponseEntity.status(ResponseCode.VALIDATION_ERROR.httpStatus())
                .body(ApiResponse.failure(ResponseCode.VALIDATION_ERROR, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
        return ResponseEntity.status(ResponseCode.INTERNAL_ERROR.httpStatus())
                .body(ApiResponse.failure(ResponseCode.INTERNAL_ERROR));
    }
}
