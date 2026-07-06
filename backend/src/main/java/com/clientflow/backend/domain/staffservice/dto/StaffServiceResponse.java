package com.clientflow.backend.domain.staffservice.dto;

import java.math.BigDecimal;

public record StaffServiceResponse(
        Long id,
        Long staffId,
        Long serviceId,
        String serviceName,
        BigDecimal price,
        Integer durationMinutes
) {
}
