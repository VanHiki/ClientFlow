package com.clientflow.backend.domain.appointment.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentCreateRequest(
        @NotNull(message = "Customer is required")
        Long customerId,

        @NotNull(message = "Service is required")
        Long serviceId,

        @NotNull(message = "Staff is required")
        Long staffId,

        @NotNull(message = "Appointment date is required")
        LocalDate appointmentDate,

        @NotNull(message = "Start time is required")
        LocalTime startTime,

        String note
) {
}
