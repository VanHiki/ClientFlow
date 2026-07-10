package com.clientflow.backend.domain.business;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.business.dto.BusinessCreateRequest;
import com.clientflow.backend.domain.business.dto.BusinessResponse;
import com.clientflow.backend.domain.business.dto.BusinessStatusUpdateRequest;
import com.clientflow.backend.domain.business.dto.BusinessUpdateRequest;
import com.clientflow.backend.domain.business.mapper.BusinessMapper;
import com.clientflow.backend.domain.user.User;
import com.clientflow.backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.clientflow.backend.common.response.PageResponse;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class BusinessService {
    BusinessMapper businessMapper;
    BusinessRepository businessRepository;
    SecurityUtil securityUtil;

    @Transactional
    public BusinessResponse createBusiness(BusinessCreateRequest request) {
        User owner = securityUtil.getCurrentUser();
        String slug = normalizeSlug(request.slug());

        if (businessRepository.existsBySlug(slug)) {
            throw new AppException(ErrorCode.BUSINESS_SLUG_ALREADY_EXISTS);
        }

        Business business = businessMapper.toEntity(request);
        business.setOwner(owner);
        business.setName(request.name().trim());
        business.setSlug(slug);
        business.setPhone(normalizeNullable(request.phone()));
        business.setEmail(normalizeEmail(request.email()));
        business.setAddress(normalizeNullable(request.address()));
        business.setActive(true);

        return businessMapper.toResponse(businessRepository.save(business));
    }

    @Transactional(readOnly = true)
    public PageResponse<BusinessResponse> getMyBusinesses(Pageable pageable) {
        Long ownerId = securityUtil.getCurrentUserId();

        return PageResponse.from(
                businessRepository.findByOwnerId(ownerId, pageable)
                        .map(businessMapper::toResponse)
        );
    }

    @Transactional(readOnly = true)
    public BusinessResponse getMyBusiness(Long businessId) {
        Business business = getCurrentOwnerBusiness(businessId);

        return businessMapper.toResponse(business);
    }

    @Transactional
    public BusinessResponse updateBusiness(Long businessId, BusinessUpdateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);
        String slug = normalizeSlug(request.slug());

        if (businessRepository.existsBySlugAndIdNot(slug, business.getId())) {
            throw new AppException(ErrorCode.BUSINESS_SLUG_ALREADY_EXISTS);
        }

        business.setName(request.name().trim());
        business.setSlug(slug);
        business.setPhone(normalizeNullable(request.phone()));
        business.setEmail(normalizeEmail(request.email()));
        business.setAddress(normalizeNullable(request.address()));

        return businessMapper.toResponse(business);
    }

    @Transactional
    public BusinessResponse updateBusinessStatus(Long businessId, BusinessStatusUpdateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);

        business.setActive(request.active());

        return businessMapper.toResponse(business);
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private String normalizeSlug(String slug) {
        return slug.trim().toLowerCase();
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }

        return email.trim().toLowerCase();
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
