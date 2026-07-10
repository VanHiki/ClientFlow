package com.clientflow.backend.domain.staff;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.staff.dto.StaffCreateRequest;
import com.clientflow.backend.domain.staff.dto.StaffResponse;
import com.clientflow.backend.domain.staff.dto.StaffStatusUpdateRequest;
import com.clientflow.backend.domain.staff.dto.StaffUpdateRequest;
import com.clientflow.backend.domain.staff.mapper.StaffProfileMapper;
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
public class StaffProfileService {

    StaffProfileRepository staffProfileRepository;
    BusinessRepository businessRepository;
    StaffProfileMapper staffProfileMapper;
    SecurityUtil securityUtil;

    @Transactional
    public StaffResponse createStaff(Long businessId, StaffCreateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);
        String email = normalizeEmail(request.email());

        if (email != null && staffProfileRepository.existsByBusinessIdAndEmailIgnoreCase(business.getId(), email)) {
            throw new AppException(ErrorCode.STAFF_EMAIL_ALREADY_EXISTS);
        }

        StaffProfile staffProfile = staffProfileMapper.toEntity(request);
        staffProfile.setBusiness(business);
        staffProfile.setFullName(request.fullName().trim());
        staffProfile.setEmail(email);
        staffProfile.setPhone(normalizeNullable(request.phone()));
        staffProfile.setPosition(normalizeNullable(request.position()));
        staffProfile.setActive(true);

        return staffProfileMapper.toResponse(staffProfileRepository.save(staffProfile));
    }

    @Transactional(readOnly = true)
    public PageResponse<StaffResponse> getStaff(Long businessId, Pageable pageable) {
        Business business = getCurrentOwnerBusiness(businessId);

        return PageResponse.from(
                staffProfileRepository.findByBusinessId(business.getId(), pageable)
                        .map(staffProfileMapper::toResponse)
        );
    }

    @Transactional
    public StaffResponse updateStaff(Long businessId, Long staffId, StaffUpdateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staffProfile = getStaffProfile(business.getId(), staffId);
        String email = normalizeEmail(request.email());

        if (email != null && staffProfileRepository.existsByBusinessIdAndEmailIgnoreCaseAndIdNot(
                business.getId(),
                email,
                staffProfile.getId()
        )) {
            throw new AppException(ErrorCode.STAFF_EMAIL_ALREADY_EXISTS);
        }

        staffProfile.setFullName(request.fullName().trim());
        staffProfile.setEmail(email);
        staffProfile.setPhone(normalizeNullable(request.phone()));
        staffProfile.setPosition(normalizeNullable(request.position()));

        return staffProfileMapper.toResponse(staffProfile);
    }

    @Transactional
    public StaffResponse updateStaffStatus(Long businessId, Long staffId, StaffStatusUpdateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staffProfile = getStaffProfile(business.getId(), staffId);

        staffProfile.setActive(request.active());

        return staffProfileMapper.toResponse(staffProfile);
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private StaffProfile getStaffProfile(Long businessId, Long staffId) {
        return staffProfileRepository.findByIdAndBusinessId(staffId, businessId)
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));
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
