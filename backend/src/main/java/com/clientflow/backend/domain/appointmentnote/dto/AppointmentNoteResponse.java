package com.clientflow.backend.domain.appointmentnote.dto;

import java.time.LocalDateTime;

public record AppointmentNoteResponse(
        Long id,
        Long appointmentId,
        Long authorUserId,
        String authorName,
        String content,
        LocalDateTime createdAt
) {
}
