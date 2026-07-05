package com.clientflow.backend.auth;

import com.clientflow.backend.auth.dto.AuthResponse;
import com.clientflow.backend.auth.dto.LoginRequest;
import com.clientflow.backend.auth.dto.RegisterRequest;
import com.clientflow.backend.common.enums.RoleName;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.domain.role.Role;
import com.clientflow.backend.domain.role.RoleRepository;
import com.clientflow.backend.domain.user.User;
import com.clientflow.backend.domain.user.UserRepository;
import com.clientflow.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        Role ownerRole = roleRepository.findByName(RoleName.OWNER)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_INITIALIZED));

        User user = User.builder()
                .role(ownerRole)
                .fullName(request.fullName().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .phone(normalizeNullable(request.phone()))
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole().getName().name(),
                token
        );
    }

    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_EMAIL_OR_PASSWORD);
        }

        if (!user.isEnabled()) {
            throw new AppException(ErrorCode.USER_DISABLED);
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().getName().name(),
                token
        );
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
