package com.clientflow.backend.domain.customer.dto;

public record CustomerResponse(
        Long id,
        Long businessId,
        String fullName,
        String phone,
        String email,
        String notes,
        boolean active
) {
}
