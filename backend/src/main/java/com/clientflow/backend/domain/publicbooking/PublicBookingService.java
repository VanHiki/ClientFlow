package com.clientflow.backend.domain.publicbooking;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.publicbooking.dto.PublicBusinessResponse;
import com.clientflow.backend.domain.publicbooking.dto.PublicServiceResponse;
import com.clientflow.backend.domain.service.ServiceOfferingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicBookingService {

    private final BusinessRepository businessRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;

    @Transactional(readOnly = true)
    public PublicBusinessResponse getBusiness(String slug) {
        Business business = getActiveBusiness(slug);

        return new PublicBusinessResponse(
                business.getId(),
                business.getName(),
                business.getSlug(),
                business.getPhone(),
                business.getEmail(),
                business.getAddress(),
                business.getTimezone()
        );
    }

    @Transactional(readOnly = true)
    public List<PublicServiceResponse> getServices(String slug) {
        Business business = getActiveBusiness(slug);

        return serviceOfferingRepository.findByBusinessIdAndActiveTrueOrderByCreatedAtDesc(business.getId())
                .stream()
                .map(service -> new PublicServiceResponse(
                        service.getId(),
                        service.getName(),
                        service.getDescription(),
                        service.getPrice(),
                        service.getDurationMinutes()
                ))
                .toList();
    }

    private Business getActiveBusiness(String slug) {
        Business business = businessRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));

        if (!business.isActive()) {
            throw new AppException(ErrorCode.BUSINESS_NOT_FOUND);
        }

        return business;
    }
}