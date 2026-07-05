package com.clientflow.backend.domain.business.dto;

public record BusinessResponse(
        Long id,
        String name,
        String slug,
        String phone,
        String email,
        String address,
        String timezone,
        boolean active
) {
}
