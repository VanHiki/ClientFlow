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
    SERVICE_NOT_FOUND(1010, "Service not found", HttpStatus.NOT_FOUND),
    SERVICE_NAME_ALREADY_EXISTS(1011, "Service name already exists", HttpStatus.CONFLICT),
    INVALID_SERVICE_PRICE(1012, "Service price must not be negative", HttpStatus.BAD_REQUEST),
    INVALID_SERVICE_DURATION(1013, "Service duration must be greater than zero", HttpStatus.BAD_REQUEST),
    STAFF_EMAIL_ALREADY_EXISTS(1014, "Staff email already exists in this business", HttpStatus.CONFLICT),
    STAFF_NOT_FOUND(1015, "Staff not found", HttpStatus.NOT_FOUND),
    STAFF_SERVICE_ALREADY_ASSIGNED(1016, "Staff already assigned to this service", HttpStatus.CONFLICT),
    INVALID_WORKING_HOUR_RANGE(1017, "Working hour start time must be before end time", HttpStatus.BAD_REQUEST),
    WORKING_HOUR_OVERLAP(1018, "Working hour overlaps with existing working hour", HttpStatus.CONFLICT),
    CUSTOMER_NOT_FOUND(1019, "Customer not found", HttpStatus.NOT_FOUND),
    CUSTOMER_PHONE_ALREADY_EXISTS(1020, "Customer phone already exists in this business", HttpStatus.CONFLICT),
    CUSTOMER_EMAIL_ALREADY_EXISTS(1021, "Customer email already exists in this business", HttpStatus.CONFLICT),
    APPOINTMENT_NOT_FOUND(1022, "Appointment not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_IN_PAST(1023, "Cannot book appointment in the past", HttpStatus.BAD_REQUEST),
    APPOINTMENT_OUTSIDE_WORKING_HOURS(1024, "Appointment is outside staff working hours", HttpStatus.BAD_REQUEST),
    APPOINTMENT_OVERLAP(1025, "Staff already has an appointment in this time range", HttpStatus.CONFLICT),
    STAFF_NOT_ASSIGNED_TO_SERVICE(1026, "Staff is not assigned to this service", HttpStatus.BAD_REQUEST),
    SERVICE_INACTIVE(1027, "Service is inactive", HttpStatus.BAD_REQUEST),
    INVALID_APPOINTMENT_STATUS_TRANSITION(1028, "Invalid appointment status transition", HttpStatus.BAD_REQUEST),
    STAFF_TIME_OFF_NOT_FOUND(1029, "Staff time off not found", HttpStatus.NOT_FOUND),
    INVALID_STAFF_TIME_OFF_RANGE(1030, "Staff time off start time must be before end time", HttpStatus.BAD_REQUEST),
    STAFF_TIME_OFF_OVERLAP(1031, "Staff time off overlaps with existing time off", HttpStatus.CONFLICT),
    APPOINTMENT_DURING_STAFF_TIME_OFF(1032, "Appointment is during staff time off", HttpStatus.BAD_REQUEST),
    BUSINESS_EXCEPTION_NOT_FOUND(1033, "Business exception not found", HttpStatus.NOT_FOUND),
    BUSINESS_EXCEPTION_ALREADY_EXISTS(1034, "Business exception already exists on this date", HttpStatus.CONFLICT),
    APPOINTMENT_ON_BUSINESS_EXCEPTION(1035, "Appointment date is closed for this business", HttpStatus.BAD_REQUEST),
    STAFF_INACTIVE(1036, "Staff is inactive", HttpStatus.BAD_REQUEST),
    CUSTOMER_INACTIVE(1037, "Customer is inactive", HttpStatus.BAD_REQUEST),
    WORKING_HOUR_NOT_FOUND(1038, "Working hour not found", HttpStatus.NOT_FOUND),
    STAFF_SERVICE_NOT_FOUND(1039, "Staff service assignment not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_NOTE_NOT_FOUND(1040, "Appointment note not found", HttpStatus.NOT_FOUND),
    INVALID_DATE_RANGE(1041, "From date must not be after to date", HttpStatus.BAD_REQUEST),
    STAFF_ACCOUNT_ALREADY_EXISTS(1042, "Staff account already exists", HttpStatus.CONFLICT),
    STAFF_EMAIL_REQUIRED(1043, "Staff email is required for a login account", HttpStatus.BAD_REQUEST),
    PUBLIC_CANCELLATION_NOT_ALLOWED(1044, "This appointment cannot be cancelled", HttpStatus.BAD_REQUEST),
    PUBLIC_CANCELLATION_TOO_LATE(1045, "Appointment cancellation notice is too short", HttpStatus.BAD_REQUEST),
    PUBLIC_BOOKING_RATE_LIMITED(1046, "Public booking rate limit exceeded", HttpStatus.TOO_MANY_REQUESTS);
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
