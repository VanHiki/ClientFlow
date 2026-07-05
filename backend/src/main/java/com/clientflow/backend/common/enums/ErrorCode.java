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
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
