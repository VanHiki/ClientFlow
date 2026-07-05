package com.clientflow.backend.domain.staff.mapper;

import com.clientflow.backend.domain.staff.StaffProfile;
import com.clientflow.backend.domain.staff.dto.StaffCreateRequest;
import com.clientflow.backend.domain.staff.dto.StaffResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StaffProfileMapper {

    @Mapping(target = "businessId", source = "business.id")
    @Mapping(target = "userId", source = "user.id")
    StaffResponse toResponse(StaffProfile staffProfile);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "business", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    StaffProfile toEntity(StaffCreateRequest request);
}
