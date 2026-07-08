package com.clientflow.backend.domain.stafftimeoff.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record StaffTimeOffResponse(
        Long id,
        Long staffId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String reason
) {
}