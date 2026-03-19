package com.devflow.api.common.exception;

import com.devflow.api.common.api.ResponseCode;

public class BusinessException extends RuntimeException {

    private final ResponseCode responseCode;

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.message());
        this.responseCode = responseCode;
    }

    public BusinessException(ResponseCode responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public ResponseCode getResponseCode() {
        return responseCode;
    }
}
