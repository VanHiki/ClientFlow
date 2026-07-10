package com.clientflow.backend.domain.notification;

import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.enums.NotificationType;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.appointment.Appointment;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.notification.dto.NotificationResponse;
import com.clientflow.backend.domain.notification.dto.UnreadNotificationCountResponse;
import com.clientflow.backend.domain.notification.mapper.NotificationMapper;
import com.clientflow.backend.domain.user.User;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {

    NotificationRepository notificationRepository;
    BusinessRepository businessRepository;
    NotificationMapper notificationMapper;
    SecurityUtil securityUtil;

    @Transactional
    public void recordAppointmentCreated(Appointment appointment) {
        saveForRecipients(
                appointment,
                NotificationType.APPOINTMENT_CREATED,
                "New appointment",
                appointment.getCustomer().getFullName()
                        + " booked " + appointment.getServiceOffering().getName()
                        + " on " + appointment.getAppointmentDate()
                        + " at " + appointment.getStartTime()
        );
    }

    @Transactional
    public void recordAppointmentStatusChanged(Appointment appointment) {
        NotificationType type = toNotificationType(appointment.getStatus());
        if (type == null) {
            return;
        }

        saveForRecipients(
                appointment,
                type,
                "Appointment " + appointment.getStatus().name().toLowerCase(),
                "Appointment " + appointment.getBookingCode()
                        + " is now " + appointment.getStatus().name()
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getOwnerNotifications(Long businessId, Pageable pageable) {
        Business business = getCurrentOwnerBusiness(businessId);
        Long recipientId = securityUtil.getCurrentUserId();

        return PageResponse.from(
                notificationRepository.findByBusinessIdAndRecipientId(business.getId(), recipientId, pageable)
                        .map(notificationMapper::toResponse)
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> getStaffNotifications(Pageable pageable) {
        Long recipientId = securityUtil.getCurrentUserId();

        return PageResponse.from(
                notificationRepository.findByRecipientId(recipientId, pageable)
                        .map(notificationMapper::toResponse)
        );
    }

    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse getOwnerUnreadCount(Long businessId) {
        Business business = getCurrentOwnerBusiness(businessId);
        long count = notificationRepository.countByBusinessIdAndRecipientIdAndReadAtIsNull(
                business.getId(),
                securityUtil.getCurrentUserId()
        );

        return new UnreadNotificationCountResponse(count);
    }

    @Transactional(readOnly = true)
    public UnreadNotificationCountResponse getStaffUnreadCount() {
        long count = notificationRepository.countByRecipientIdAndReadAtIsNull(securityUtil.getCurrentUserId());
        return new UnreadNotificationCountResponse(count);
    }

    @Transactional
    public NotificationResponse markAsRead(Long notificationId) {
        Long recipientId = securityUtil.getCurrentUserId();
        Notification notification = notificationRepository.findByIdAndRecipientId(notificationId, recipientId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        markRead(notification);
        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public NotificationResponse markOwnerAsRead(Long businessId, Long notificationId) {
        Business business = getCurrentOwnerBusiness(businessId);
        Notification notification = notificationRepository.findByIdAndBusinessIdAndRecipientId(
                        notificationId,
                        business.getId(),
                        securityUtil.getCurrentUserId()
                )
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));

        markRead(notification);
        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public void markAllOwnerAsRead(Long businessId) {
        Business business = getCurrentOwnerBusiness(businessId);
        List<Notification> notifications = notificationRepository
                .findByBusinessIdAndRecipientIdAndReadAtIsNull(
                        business.getId(),
                        securityUtil.getCurrentUserId()
                );

        notifications.forEach(this::markRead);
    }

    @Transactional
    public void markAllStaffAsRead() {
        List<Notification> notifications = notificationRepository
                .findByRecipientIdAndReadAtIsNull(securityUtil.getCurrentUserId());

        notifications.forEach(this::markRead);
    }

    private void saveForRecipients(
            Appointment appointment,
            NotificationType type,
            String title,
            String message
    ) {
        Set<User> recipients = new LinkedHashSet<>();
        recipients.add(appointment.getBusiness().getOwner());

        if (appointment.getStaffProfile().getUser() != null) {
            recipients.add(appointment.getStaffProfile().getUser());
        }

        List<Notification> notifications = recipients.stream()
                .map(recipient -> Notification.builder()
                        .business(appointment.getBusiness())
                        .recipient(recipient)
                        .appointment(appointment)
                        .type(type)
                        .title(title)
                        .message(message)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }

    private NotificationType toNotificationType(AppointmentStatus status) {
        return switch (status) {
            case CONFIRMED -> NotificationType.APPOINTMENT_CONFIRMED;
            case CANCELLED -> NotificationType.APPOINTMENT_CANCELLED;
            case COMPLETED -> NotificationType.APPOINTMENT_COMPLETED;
            default -> null;
        };
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        return businessRepository.findByIdAndOwnerId(businessId, securityUtil.getCurrentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private void markRead(Notification notification) {
        if (notification.getReadAt() == null) {
            notification.setReadAt(LocalDateTime.now(BookingConstants.DEFAULT_ZONE_ID));
        }
    }
}
