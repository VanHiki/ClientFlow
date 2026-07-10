package com.clientflow.backend.domain.dashboard.dto;

public record TopServiceResponse(
        Long serviceId,
        String serviceName,
        long bookingCount
) {
}
