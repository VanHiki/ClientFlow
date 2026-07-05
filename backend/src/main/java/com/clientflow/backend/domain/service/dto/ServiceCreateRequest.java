package com.clientflow.backend.domain.service.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ServiceCreateRequest(
        @NotBlank(message = "Service name is required")
        @Size(max = 140)
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price must not be negative")
        BigDecimal price,

        @NotNull(message = "Duration is required")
        @Min(value = 1, message = "Duration must be greater than zero")
        Integer durationMinutes
) {
}
