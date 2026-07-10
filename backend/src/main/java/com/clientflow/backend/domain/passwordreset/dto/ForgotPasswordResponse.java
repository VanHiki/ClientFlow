package com.clientflow.backend.domain.passwordreset.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ForgotPasswordResponse(String resetToken) {
}
