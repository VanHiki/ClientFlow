package com.clientflow.backend.domain.appointment.dto;

import com.clientflow.backend.common.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record AppointmentResponse(
        Long id,
        Long businessId,
        Long customerId,
        Long serviceId,
        Long staffId,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status,
        String timezone,
        String note
) {
}
