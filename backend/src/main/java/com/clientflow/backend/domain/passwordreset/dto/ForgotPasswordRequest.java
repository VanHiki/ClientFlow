package com.clientflow.backend.domain.passwordreset.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        @Size(max = 160, message = "Email must be at most 160 characters")
        String email
) {
}
