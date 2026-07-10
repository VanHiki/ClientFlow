package com.clientflow.backend.domain.service;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.service.dto.ServiceCreateRequest;
import com.clientflow.backend.domain.service.dto.ServiceResponse;
import com.clientflow.backend.domain.service.dto.ServiceStatusUpdateRequest;
import com.clientflow.backend.domain.service.dto.ServiceUpdateRequest;
import com.clientflow.backend.domain.service.mapper.ServiceOfferingMapper;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceOfferingService {


    ServiceOfferingRepository serviceOfferingRepository;
    BusinessRepository businessRepository;
    SecurityUtil securityUtil;
    ServiceOfferingMapper serviceOfferingMapper;


    @Transactional
    public ServiceResponse createService(Long businessId, ServiceCreateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);

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

    @Transactional(readOnly = true)
    public PageResponse<ServiceResponse> getServices(Long businessId, Pageable pageable) {
        Business business = getCurrentOwnerBusiness(businessId);

        return PageResponse.from(
                serviceOfferingRepository.findByBusinessId(business.getId(), pageable)
                        .map(serviceOfferingMapper::toResponse)
        );
    }

    @Transactional
    public ServiceResponse updateService(Long businessId, Long serviceId, ServiceUpdateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);
        ServiceOffering serviceOffering = getServiceOffering(business.getId(), serviceId);
        String name = request.name().trim();

        if (serviceOfferingRepository.existsByBusinessIdAndNameIgnoreCaseAndIdNot(
                business.getId(),
                name,
                serviceOffering.getId()
        )) {
            throw new AppException(ErrorCode.SERVICE_NAME_ALREADY_EXISTS);
        }

        serviceOffering.setName(name);
        serviceOffering.setDescription(normalizeNullable(request.description()));
        serviceOffering.setPrice(request.price());
        serviceOffering.setDurationMinutes(request.durationMinutes());

        return serviceOfferingMapper.toResponse(serviceOffering);
    }

    @Transactional
    public ServiceResponse updateServiceStatus(
            Long businessId,
            Long serviceId,
            ServiceStatusUpdateRequest request
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        ServiceOffering serviceOffering = getServiceOffering(business.getId(), serviceId);

        serviceOffering.setActive(request.active());

        return serviceOfferingMapper.toResponse(serviceOffering);
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private ServiceOffering getServiceOffering(Long businessId, Long serviceId) {
        return serviceOfferingRepository.findByIdAndBusinessId(serviceId, businessId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
