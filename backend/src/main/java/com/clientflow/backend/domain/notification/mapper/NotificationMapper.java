package com.clientflow.backend.domain.notification.mapper;

import com.clientflow.backend.domain.notification.Notification;
import com.clientflow.backend.domain.notification.dto.NotificationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "businessId", source = "business.id")
    @Mapping(target = "appointmentId", source = "appointment.id")
    NotificationResponse toResponse(Notification notification);
}
