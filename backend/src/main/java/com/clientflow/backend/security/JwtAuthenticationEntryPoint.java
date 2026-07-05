package com.clientflow.backend.security;

import com.clientflow.backend.common.enums.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorCode errorCode = ErrorCode.UNAUTHENTICATED;
        String body = """
                {"code":%d,"message":"%s"}
                """.formatted(errorCode.getCode(), errorCode.getMessage());

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(body);
        response.flushBuffer();
    }
}
