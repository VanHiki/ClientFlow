package com.clientflow.backend.domain.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerCreateRequest(
        @NotBlank(message = "Customer full name is required")
        @Size(max = 120, message = "Customer full name must be at most 120 characters")
        String fullName,

        @NotBlank(message = "Customer phone is required")
        @Size(max = 30, message = "Customer phone must be at most 30 characters")
        String phone,

        @Email(message = "Customer email is invalid")
        @Size(max = 160, message = "Customer email must be at most 160 characters")
        String email,

        String notes
) {
}
