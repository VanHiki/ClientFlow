package com.clientflow.backend.domain.staff.dto;

import jakarta.validation.constraints.NotNull;

public record StaffStatusUpdateRequest(
        @NotNull(message = "Staff active status is required")
        Boolean active
) {
}
