package com.clientflow.backend.domain.businessexception;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.businessexception.dto.BusinessExceptionCreateRequest;
import com.clientflow.backend.domain.businessexception.dto.BusinessExceptionResponse;
import com.clientflow.backend.domain.businessexception.dto.BusinessExceptionUpdateRequest;
import com.clientflow.backend.domain.businessexception.mapper.BusinessExceptionDayMapper;
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
public class BusinessExceptionDayService {

    BusinessExceptionDayRepository businessExceptionDayRepository;
    BusinessRepository businessRepository;
    BusinessExceptionDayMapper businessExceptionDayMapper;
    SecurityUtil securityUtil;

    @Transactional
    public BusinessExceptionResponse createException(Long businessId, BusinessExceptionCreateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);

        if (businessExceptionDayRepository.existsByBusinessIdAndDate(business.getId(), request.date())) {
            throw new AppException(ErrorCode.BUSINESS_EXCEPTION_ALREADY_EXISTS);
        }

        BusinessExceptionDay exceptionDay = BusinessExceptionDay.builder()
                .business(business)
                .date(request.date())
                .type(request.type())
                .reason(normalizeNullable(request.reason()))
                .build();

        return businessExceptionDayMapper.toResponse(businessExceptionDayRepository.save(exceptionDay));
    }

    @Transactional(readOnly = true)
    public PageResponse<BusinessExceptionResponse> getExceptions(Long businessId, Pageable pageable) {
        Business business = getCurrentOwnerBusiness(businessId);

        return PageResponse.from(
                businessExceptionDayRepository.findByBusinessId(business.getId(), pageable)
                        .map(businessExceptionDayMapper::toResponse)
        );
    }

    @Transactional
    public BusinessExceptionResponse updateException(
            Long businessId,
            Long exceptionId,
            BusinessExceptionUpdateRequest request
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        BusinessExceptionDay exceptionDay = getException(business.getId(), exceptionId);

        if (businessExceptionDayRepository.existsByBusinessIdAndDateAndIdNot(
                business.getId(),
                request.date(),
                exceptionDay.getId()
        )) {
            throw new AppException(ErrorCode.BUSINESS_EXCEPTION_ALREADY_EXISTS);
        }

        exceptionDay.setDate(request.date());
        exceptionDay.setType(request.type());
        exceptionDay.setReason(normalizeNullable(request.reason()));

        return businessExceptionDayMapper.toResponse(exceptionDay);
    }

    @Transactional
    public void deleteException(Long businessId, Long exceptionId) {
        Business business = getCurrentOwnerBusiness(businessId);
        BusinessExceptionDay exceptionDay = getException(business.getId(), exceptionId);

        businessExceptionDayRepository.delete(exceptionDay);
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private BusinessExceptionDay getException(Long businessId, Long exceptionId) {
        return businessExceptionDayRepository.findByIdAndBusinessId(exceptionId, businessId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_EXCEPTION_NOT_FOUND));
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
