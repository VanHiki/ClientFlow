package com.clientflow.backend.domain.workinghour.dto;

import jakarta.validation.constraints.NotNull;

public record WorkingHourStatusUpdateRequest(
        @NotNull(message = "Working hour active status is required")
        Boolean active
) {
}
