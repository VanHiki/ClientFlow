package com.clientflow.backend.domain.business.mapper;

import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.dto.BusinessCreateRequest;
import com.clientflow.backend.domain.business.dto.BusinessResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusinessMapper {

    BusinessResponse toResponse(Business business);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "timezone", ignore = true)
    @Mapping(target = "active", ignore = true)
    Business toEntity(BusinessCreateRequest businessCreateRequest);
}
