package com.clientflow.backend.domain.appointment.dto;

import com.clientflow.backend.common.enums.AppointmentStatus;
import jakarta.validation.constraints.NotNull;

public record AppointmentStatusUpdateRequest(
        @NotNull(message = "Appointment status is required")
        AppointmentStatus status
) {
}
