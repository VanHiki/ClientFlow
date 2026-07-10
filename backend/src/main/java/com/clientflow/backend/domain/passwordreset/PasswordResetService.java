package com.clientflow.backend.domain.passwordreset;

import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.passwordreset.dto.ForgotPasswordRequest;
import com.clientflow.backend.domain.passwordreset.dto.ForgotPasswordResponse;
import com.clientflow.backend.domain.passwordreset.dto.ResetPasswordRequest;
import com.clientflow.backend.domain.user.User;
import com.clientflow.backend.domain.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordResetService {

    private static final int TOKEN_BYTES = 32;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final long expirationMinutes;
    private final boolean exposeToken;
    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            PasswordResetTokenRepository passwordResetTokenRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${clientflow.password-reset.expiration-minutes:15}") long expirationMinutes,
            @Value("${clientflow.password-reset.expose-token:false}") boolean exposeToken
    ) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.expirationMinutes = expirationMinutes;
        this.exposeToken = exposeToken;
    }

    @Transactional
    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        String rawToken = generateRawToken();
        String email = request.email().trim().toLowerCase();

        userRepository.findByEmail(email).ifPresent(user -> createToken(user, rawToken));

        return new ForgotPasswordResponse(exposeToken ? rawToken : null);
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        LocalDateTime now = LocalDateTime.now(BookingConstants.DEFAULT_ZONE_ID);
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(hash(request.token().trim()))
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN));

        if (token.getUsedAt() != null || !now.isBefore(token.getExpiresAt())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN);
        }

        token.getUser().setPasswordHash(passwordEncoder.encode(request.newPassword()));
        token.setUsedAt(now);

        passwordResetTokenRepository.findByUserIdAndUsedAtIsNull(token.getUser().getId())
                .stream()
                .filter(otherToken -> !otherToken.getId().equals(token.getId()))
                .forEach(otherToken -> otherToken.setUsedAt(now));
    }

    private void createToken(User user, String rawToken) {
        LocalDateTime now = LocalDateTime.now(BookingConstants.DEFAULT_ZONE_ID);

        passwordResetTokenRepository.findByUserIdAndUsedAtIsNull(user.getId())
                .forEach(token -> token.setUsedAt(now));

        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .tokenHash(hash(rawToken))
                .expiresAt(now.plusMinutes(expirationMinutes))
                .build();

        passwordResetTokenRepository.save(token);
    }

    private String generateRawToken() {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
