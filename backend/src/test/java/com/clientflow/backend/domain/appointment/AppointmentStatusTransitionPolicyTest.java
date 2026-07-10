package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AppointmentStatusTransitionPolicyTest {

    @Test
    void allowsValidTransitions() {
        assertDoesNotThrow(() -> AppointmentStatusTransitionPolicy.validate(
                AppointmentStatus.PENDING,
                AppointmentStatus.CONFIRMED
        ));
        assertDoesNotThrow(() -> AppointmentStatusTransitionPolicy.validate(
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.CHECKED_IN
        ));
        assertDoesNotThrow(() -> AppointmentStatusTransitionPolicy.validate(
                AppointmentStatus.CHECKED_IN,
                AppointmentStatus.COMPLETED
        ));
        assertDoesNotThrow(() -> AppointmentStatusTransitionPolicy.validate(
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.CANCELLED
        ));
    }

    @Test
    void rejectsTransitionFromFinalStatus() {
        AppException exception = assertThrows(AppException.class, () ->
                AppointmentStatusTransitionPolicy.validate(
                        AppointmentStatus.COMPLETED,
                        AppointmentStatus.CONFIRMED
                )
        );

        assertEquals(ErrorCode.INVALID_APPOINTMENT_STATUS_TRANSITION, exception.getErrorCode());
    }
}
