package com.clientflow.backend.domain.business.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BusinessUpdateRequest(
        @NotBlank(message = "Business name is required")
        @Size(max = 160, message = "Business name must be at most 160 characters")
        String name,

        @NotBlank(message = "Business slug is required")
        @Size(max = 120, message = "Business slug must be at most 120 characters")
        @Pattern(
                regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
                message = "Business slug can only contain lowercase letters, numbers, and hyphens"
        )
        String slug,

        @Size(max = 30, message = "Phone must be at most 30 characters")
        String phone,

        @Email(message = "Email is invalid")
        @Size(max = 160, message = "Email must be at most 160 characters")
        String email,

        @Size(max = 255, message = "Address must be at most 255 characters")
        String address
) {
}
