package com.clientflow.backend.domain.customer.dto;

import jakarta.validation.constraints.NotNull;

public record CustomerStatusUpdateRequest(
        @NotNull(message = "Customer active status is required")
        Boolean active
) {
}
