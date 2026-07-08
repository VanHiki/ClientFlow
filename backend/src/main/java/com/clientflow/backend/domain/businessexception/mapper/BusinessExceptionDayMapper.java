package com.clientflow.backend.domain.businessexception.mapper;

import com.clientflow.backend.domain.businessexception.BusinessExceptionDay;
import com.clientflow.backend.domain.businessexception.dto.BusinessExceptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusinessExceptionDayMapper {

    @Mapping(target = "businessId", source = "business.id")
    BusinessExceptionResponse toResponse(BusinessExceptionDay businessExceptionDay);
}
