package com.clientflow.backend.domain.notification;

import com.clientflow.backend.common.response.ApiResponse;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.notification.dto.NotificationResponse;
import com.clientflow.backend.domain.notification.dto.UnreadNotificationCountResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/businesses/{businessId}/notifications")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OwnerNotificationController {

    NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<PageResponse<NotificationResponse>> getNotifications(
            @PathVariable Long businessId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ApiResponse.<PageResponse<NotificationResponse>>builder()
                .message("Get notifications successfully")
                .result(notificationService.getOwnerNotifications(businessId, pageable))
                .build();
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<UnreadNotificationCountResponse> getUnreadCount(@PathVariable Long businessId) {
        return ApiResponse.<UnreadNotificationCountResponse>builder()
                .message("Get unread notification count successfully")
                .result(notificationService.getOwnerUnreadCount(businessId))
                .build();
    }

    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<NotificationResponse> markAsRead(
            @PathVariable Long businessId,
            @PathVariable Long notificationId
    ) {
        return ApiResponse.<NotificationResponse>builder()
                .message("Notification marked as read successfully")
                .result(notificationService.markOwnerAsRead(businessId, notificationId))
                .build();
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasAuthority('ROLE_OWNER')")
    public ApiResponse<Void> markAllAsRead(@PathVariable Long businessId) {
        notificationService.markAllOwnerAsRead(businessId);

        return ApiResponse.<Void>builder()
                .message("All notifications marked as read successfully")
                .build();
    }
}
