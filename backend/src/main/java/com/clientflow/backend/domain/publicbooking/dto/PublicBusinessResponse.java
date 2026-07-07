package com.clientflow.backend.domain.publicbooking.dto;

public record PublicBusinessResponse(
        Long id,
        String name,
        String slug,
        String phone,
        String email,
        String address,
        String timezone
) {
}
