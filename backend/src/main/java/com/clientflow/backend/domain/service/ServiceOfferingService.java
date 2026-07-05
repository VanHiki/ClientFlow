package com.clientflow.backend.domain.service;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.service.dto.ServiceCreateRequest;
import com.clientflow.backend.domain.service.dto.ServiceResponse;
import com.clientflow.backend.domain.service.mapper.ServiceOfferingMapper;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceOfferingService {


    ServiceOfferingRepository serviceOfferingRepository;
    BusinessRepository businessRepository;
    SecurityUtil securityUtil;
    ServiceOfferingMapper serviceOfferingMapper;


    public ServiceResponse createService(ServiceCreateRequest request) {
        Business business = getCurrentOwnerBusiness();

        String name = request.name().trim();

        if (serviceOfferingRepository.existsByBusinessIdAndNameIgnoreCase(business.getId(), name)) {
            throw new AppException(ErrorCode.SERVICE_NAME_ALREADY_EXISTS);
        }

        ServiceOffering serviceOffering = serviceOfferingMapper.toEntity(request);
        serviceOffering.setBusiness(business);
        serviceOffering.setName(name);
        serviceOffering.setDescription(normalizeNullable(request.description()));
        serviceOffering.setActive(true);

        return serviceOfferingMapper.toResponse(serviceOfferingRepository.save(serviceOffering));
    }

    private Business getCurrentOwnerBusiness() {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
