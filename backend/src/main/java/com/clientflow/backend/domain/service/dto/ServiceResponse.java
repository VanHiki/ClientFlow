package com.clientflow.backend.domain.service.dto;

import java.math.BigDecimal;

public record ServiceResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer durationMinutes,
        boolean active
) {

}
