package com.clientflow.backend.domain.businessexception.dto;

import com.clientflow.backend.common.enums.BusinessExceptionType;
import java.time.LocalDate;

public record BusinessExceptionResponse(
        Long id,
        Long businessId,
        LocalDate date,
        BusinessExceptionType type,
        String reason
) {
}
