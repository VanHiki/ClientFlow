package com.clientflow.backend.domain.staff.dto;

public record StaffResponse(
        Long id,
        Long businessId,
        Long userId,
        String fullName,
        String email,
        String phone,
        String position,
        boolean active
) {
}
