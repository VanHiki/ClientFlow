package com.clientflow.backend.auth;

import com.clientflow.backend.auth.dto.AuthResponse;
import com.clientflow.backend.auth.dto.LoginRequest;
import com.clientflow.backend.auth.dto.RegisterRequest;
import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.domain.passwordreset.PasswordResetService;
import com.clientflow.backend.domain.passwordreset.dto.ForgotPasswordRequest;
import com.clientflow.backend.domain.passwordreset.dto.ForgotPasswordResponse;
import com.clientflow.backend.domain.passwordreset.dto.ResetPasswordRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;
    PasswordResetService passwordResetService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.register(request))
                .message("Register successfully")
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.<AuthResponse>builder()
                .result(authService.login(request))
                .message("Login successfully")
                .build();
    }

    @PostMapping("/forgot-password")
    public ApiResponse<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        return ApiResponse.<ForgotPasswordResponse>builder()
                .message("If the email exists, password reset instructions have been created")
                .result(passwordResetService.forgotPassword(request))
                .build();
    }

    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);

        return ApiResponse.<Void>builder()
                .message("Password reset successfully")
                .build();
    }
}
