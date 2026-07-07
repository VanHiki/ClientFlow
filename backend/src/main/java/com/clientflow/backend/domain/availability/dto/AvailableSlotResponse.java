package com.clientflow.backend.domain.availability.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record AvailableSlotResponse(
        Long staffId,
        String staffName,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime
) {
}
