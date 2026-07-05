package com.clientflow.backend.domain.service.mapper;

import com.clientflow.backend.domain.service.ServiceOffering;
import com.clientflow.backend.domain.service.dto.ServiceCreateRequest;
import com.clientflow.backend.domain.service.dto.ServiceResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceOfferingMapper {

    ServiceResponse toResponse(ServiceOffering serviceOffering);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "business", ignore = true)
    @Mapping(target = "active", ignore = true)
    ServiceOffering toEntity(ServiceCreateRequest request);
}