package com.clientflow.backend.domain.dashboard.dto;

import com.clientflow.backend.common.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

public record UpcomingAppointmentResponse(
        Long id,
        String customerName,
        String serviceName,
        String staffName,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status
) {
}
