package com.clientflow.backend.security;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.user.User;
import com.clientflow.backend.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final UserRepository userRepository;

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String email = jwt.getSubject();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!user.isEnabled()) {
            throw new AppException(ErrorCode.USER_DISABLED);
        }

        return user;
    }
}
