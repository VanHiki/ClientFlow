package com.clientflow.backend.domain.business.dto;

import jakarta.validation.constraints.NotNull;

public record BusinessStatusUpdateRequest(
        @NotNull(message = "Business active status is required")
        Boolean active
) {
}
