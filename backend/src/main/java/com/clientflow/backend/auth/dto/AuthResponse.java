package com.clientflow.backend.auth.dto;


public record AuthResponse(
        Long userId,
        String fullName,
        String email,
        String role

) {
}
