package com.clientflow.backend.domain.appointment.mapper;

import com.clientflow.backend.domain.appointment.Appointment;
import com.clientflow.backend.domain.appointment.dto.AppointmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

    @Mapping(target = "businessId", source = "business.id")
    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "serviceId", source = "serviceOffering.id")
    @Mapping(target = "staffId", source = "staffProfile.id")
    AppointmentResponse toResponse(Appointment appointment);
}