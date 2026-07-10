package com.clientflow.backend.domain.staff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StaffUpdateRequest(
        @NotBlank(message = "Staff full name is required")
        @Size(max = 120, message = "Staff full name must be at most 120 characters")
        String fullName,

        @Email(message = "Staff email is invalid")
        @Size(max = 160, message = "Staff email must be at most 160 characters")
        String email,

        @Size(max = 30, message = "Staff phone must be at most 30 characters")
        String phone,

        @Size(max = 80, message = "Staff position must be at most 80 characters")
        String position
) {
}
