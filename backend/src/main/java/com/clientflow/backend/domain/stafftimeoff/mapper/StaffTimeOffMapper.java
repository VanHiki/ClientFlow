package com.clientflow.backend.domain.stafftimeoff.mapper;

import com.clientflow.backend.domain.stafftimeoff.StaffTimeOff;
import com.clientflow.backend.domain.stafftimeoff.dto.StaffTimeOffResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StaffTimeOffMapper {

    @Mapping(target = "staffId", source = "staffProfile.id")
    StaffTimeOffResponse toResponse(StaffTimeOff staffTimeOff);
}