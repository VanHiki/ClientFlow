package com.clientflow.backend.domain.service.dto;

import jakarta.validation.constraints.NotNull;

public record ServiceStatusUpdateRequest(
        @NotNull(message = "Service active status is required")
        Boolean active
) {
}
