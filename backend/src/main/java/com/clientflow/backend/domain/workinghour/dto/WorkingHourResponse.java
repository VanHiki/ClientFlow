package com.clientflow.backend.domain.workinghour.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record WorkingHourResponse(
        Long id,
        Long staffId,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        boolean active

) {
}
