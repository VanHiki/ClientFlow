package com.clientflow.backend.common.enums;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    SUCCESS(1000, "Success", HttpStatus.OK),

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1001, "Invalid request", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1002, "Email already exists", HttpStatus.CONFLICT),
    ROLE_NOT_INITIALIZED(1003, "Role is not initialized", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_EMAIL_OR_PASSWORD(1004, "Invalid email or password", HttpStatus.UNAUTHORIZED),
    USER_DISABLED(1005, "User is disabled", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    BUSINESS_SLUG_ALREADY_EXISTS(1007, "Business slug already exists", HttpStatus.CONFLICT),
    BUSINESS_NOT_FOUND(1008, "Business not found", HttpStatus.NOT_FOUND),
    OWNER_ALREADY_HAS_BUSINESS(1009, "Owner already has a business", HttpStatus.CONFLICT);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
