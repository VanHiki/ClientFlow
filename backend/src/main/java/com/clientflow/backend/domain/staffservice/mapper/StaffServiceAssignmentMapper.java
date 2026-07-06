package com.clientflow.backend.domain.staffservice.mapper;

import com.clientflow.backend.domain.staffservice.StaffServiceAssignment;
import com.clientflow.backend.domain.staffservice.dto.StaffServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StaffServiceAssignmentMapper {
    @Mapping(target = "staffId", source = "staffProfile.id")
    @Mapping(target = "serviceId", source = "serviceOffering.id")
    @Mapping(target = "serviceName", source = "serviceOffering.name")
    @Mapping(target = "price", source = "serviceOffering.price")
    @Mapping(target = "durationMinutes", source = "serviceOffering.durationMinutes")
    StaffServiceResponse toResponse(StaffServiceAssignment assignment);
}
