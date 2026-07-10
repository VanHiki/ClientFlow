package com.clientflow.backend.domain.businessexception.dto;

import com.clientflow.backend.common.enums.BusinessExceptionType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BusinessExceptionUpdateRequest(
        @NotNull(message = "Exception date is required")
        LocalDate date,

        @NotNull(message = "Exception type is required")
        BusinessExceptionType type,

        String reason
) {
}
