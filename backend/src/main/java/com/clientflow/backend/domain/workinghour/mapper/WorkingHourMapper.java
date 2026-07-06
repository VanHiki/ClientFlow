package com.clientflow.backend.domain.workinghour.mapper;

import com.clientflow.backend.domain.workinghour.WorkingHour;
import com.clientflow.backend.domain.workinghour.dto.WorkingHourCreateRequest;
import com.clientflow.backend.domain.workinghour.dto.WorkingHourResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkingHourMapper {
    @Mapping(target = "staffId", source = "staffProfile.id")
    WorkingHourResponse toResponse(WorkingHour workingHour);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "staffProfile", ignore = true)
    @Mapping(target = "active", ignore = true)
    WorkingHour toEntity(WorkingHourCreateRequest request);
}
