package com.clientflow.backend.domain.notification.dto;

import com.clientflow.backend.common.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long businessId,
        Long appointmentId,
        NotificationType type,
        String title,
        String message,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
}
