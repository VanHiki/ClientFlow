package com.clientflow.backend.domain.business;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.business.dto.BusinessCreateRequest;
import com.clientflow.backend.domain.business.dto.BusinessResponse;
import com.clientflow.backend.domain.business.mapper.BusinessMapper;
import com.clientflow.backend.domain.user.User;
import com.clientflow.backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (businessRepository.findByOwnerId(owner.getId()).isPresent()) {
            throw new AppException(ErrorCode.OWNER_ALREADY_HAS_BUSINESS);
        }

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
    public BusinessResponse getMyBusiness() {
        Long ownerId = securityUtil.getCurrentUserId();
        Business business = businessRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));

        return businessMapper.toResponse(business);
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
