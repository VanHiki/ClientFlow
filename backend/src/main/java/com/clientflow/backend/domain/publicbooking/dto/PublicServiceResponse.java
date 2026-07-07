package com.clientflow.backend.domain.publicbooking.dto;

import java.math.BigDecimal;

public record PublicServiceResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer durationMinutes
) {
}