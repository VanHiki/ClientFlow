package com.clientflow.backend.domain.appointmentnote.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AppointmentNoteUpdateRequest(
        @NotBlank(message = "Appointment note content is required")
        @Size(max = 2000, message = "Appointment note content must be at most 2000 characters")
        String content
) {
}
