package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;

public final class AppointmentStatusTransitionPolicy {

    private AppointmentStatusTransitionPolicy() {
    }

    public static void validate(AppointmentStatus currentStatus, AppointmentStatus nextStatus) {
        if (currentStatus == nextStatus) {
            return;
        }

        boolean valid = switch (currentStatus) {
            case PENDING -> nextStatus == AppointmentStatus.CONFIRMED
                    || nextStatus == AppointmentStatus.CANCELLED;

            case CONFIRMED -> nextStatus == AppointmentStatus.CHECKED_IN
                    || nextStatus == AppointmentStatus.COMPLETED
                    || nextStatus == AppointmentStatus.CANCELLED
                    || nextStatus == AppointmentStatus.NO_SHOW;

            case CHECKED_IN -> nextStatus == AppointmentStatus.COMPLETED
                    || nextStatus == AppointmentStatus.CANCELLED;

            case COMPLETED, CANCELLED, NO_SHOW -> false;
        };

        if (!valid) {
            throw new AppException(ErrorCode.INVALID_APPOINTMENT_STATUS_TRANSITION);
        }
    }
}
