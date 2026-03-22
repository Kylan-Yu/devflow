package com.devflow.api.common.api;

import org.springframework.http.HttpStatus;

public enum ResponseCode {
    OK("OK", "common.success", HttpStatus.OK),
    BAD_REQUEST("BAD_REQUEST", "common.bad_request", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("VALIDATION_ERROR", "common.validation_error", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("UNAUTHORIZED", "auth.unauthorized", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("FORBIDDEN", "auth.forbidden", HttpStatus.FORBIDDEN),
    NOT_FOUND("NOT_FOUND", "common.not_found", HttpStatus.NOT_FOUND),
    CONFLICT("CONFLICT", "common.conflict", HttpStatus.CONFLICT),
    INTERNAL_ERROR("INTERNAL_ERROR", "common.internal_error", HttpStatus.INTERNAL_SERVER_ERROR),
    AUTH_INVALID_CREDENTIALS("AUTH_INVALID_CREDENTIALS", "auth.invalid_credentials", HttpStatus.UNAUTHORIZED),
    AUTH_INVALID_REFRESH_TOKEN("AUTH_INVALID_REFRESH_TOKEN", "auth.invalid_refresh_token", HttpStatus.UNAUTHORIZED),
    AUTH_REFRESH_TOKEN_EXPIRED("AUTH_REFRESH_TOKEN_EXPIRED", "auth.refresh_token_expired", HttpStatus.UNAUTHORIZED),
    AUTH_EMAIL_ALREADY_EXISTS("AUTH_EMAIL_ALREADY_EXISTS", "auth.email_already_exists", HttpStatus.CONFLICT),
    AUTH_USERNAME_ALREADY_EXISTS("AUTH_USERNAME_ALREADY_EXISTS", "auth.username_already_exists", HttpStatus.CONFLICT),
    AUTH_ACCOUNT_DISABLED("AUTH_ACCOUNT_DISABLED", "auth.account_disabled", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND("USER_NOT_FOUND", "user.not_found", HttpStatus.NOT_FOUND),
    ADMIN_INVALID_CREDENTIALS("ADMIN_INVALID_CREDENTIALS", "admin.invalid_credentials", HttpStatus.UNAUTHORIZED),
    POST_NOT_FOUND("POST_NOT_FOUND", "post.not_found", HttpStatus.NOT_FOUND),
    POST_EDIT_FORBIDDEN("POST_EDIT_FORBIDDEN", "post.edit_forbidden", HttpStatus.FORBIDDEN),
    CATEGORY_NOT_FOUND("CATEGORY_NOT_FOUND", "category.not_found", HttpStatus.NOT_FOUND),
    TAG_NOT_FOUND("TAG_NOT_FOUND", "tag.not_found", HttpStatus.NOT_FOUND),
    INVALID_CURSOR("INVALID_CURSOR", "feed.invalid_cursor", HttpStatus.BAD_REQUEST),
    POST_ALREADY_LIKED("POST_ALREADY_LIKED", "interaction.post_already_liked", HttpStatus.CONFLICT),
    POST_NOT_LIKED("POST_NOT_LIKED", "interaction.post_not_liked", HttpStatus.NOT_FOUND),
    POST_ALREADY_FAVORITED("POST_ALREADY_FAVORITED", "interaction.post_already_favorited", HttpStatus.CONFLICT),
    POST_NOT_FAVORITED("POST_NOT_FAVORITED", "interaction.post_not_favorited", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND("COMMENT_NOT_FOUND", "interaction.comment_not_found", HttpStatus.NOT_FOUND),
    COMMENT_DELETE_FORBIDDEN("COMMENT_DELETE_FORBIDDEN", "interaction.comment_delete_forbidden", HttpStatus.FORBIDDEN),
    FOLLOW_SELF_FORBIDDEN("FOLLOW_SELF_FORBIDDEN", "interaction.follow_self_forbidden", HttpStatus.BAD_REQUEST),
    USER_ALREADY_FOLLOWING("USER_ALREADY_FOLLOWING", "interaction.user_already_following", HttpStatus.CONFLICT),
    USER_NOT_FOLLOWING("USER_NOT_FOLLOWING", "interaction.user_not_following", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND("NOTIFICATION_NOT_FOUND", "notification.not_found", HttpStatus.NOT_FOUND),
    REPORT_NOT_FOUND("REPORT_NOT_FOUND", "report.not_found", HttpStatus.NOT_FOUND),
    REPORT_ALREADY_PENDING("REPORT_ALREADY_PENDING", "report.already_pending", HttpStatus.CONFLICT),
    REPORT_SELF_FORBIDDEN("REPORT_SELF_FORBIDDEN", "report.self_forbidden", HttpStatus.BAD_REQUEST),
    REPORT_INVALID_REVIEW("REPORT_INVALID_REVIEW", "report.invalid_review", HttpStatus.BAD_REQUEST),
    REPORT_ALREADY_REVIEWED("REPORT_ALREADY_REVIEWED", "report.already_reviewed", HttpStatus.CONFLICT),
    MEDIA_INVALID_FILE("MEDIA_INVALID_FILE", "media.invalid_file", HttpStatus.BAD_REQUEST),
    MEDIA_FILE_TOO_LARGE("MEDIA_FILE_TOO_LARGE", "media.file_too_large", HttpStatus.BAD_REQUEST),
    MEDIA_UPLOAD_FAILED("MEDIA_UPLOAD_FAILED", "media.upload_failed", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ResponseCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String code() {
        return code;
    }

    public String message() {
        return message;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
