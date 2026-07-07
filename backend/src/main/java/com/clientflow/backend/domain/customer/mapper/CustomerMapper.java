package com.clientflow.backend.domain.customer.mapper;

import com.clientflow.backend.domain.customer.Customer;
import com.clientflow.backend.domain.customer.dto.CustomerCreateRequest;
import com.clientflow.backend.domain.customer.dto.CustomerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "businessId", source = "business.id")
    CustomerResponse toResponse(Customer customer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "business", ignore = true)
    @Mapping(target = "active", ignore = true)
    Customer toEntity(CustomerCreateRequest request);
}