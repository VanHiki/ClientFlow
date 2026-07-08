package com.clientflow.backend.domain.stafftimeoff;

import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.staff.StaffProfile;
import com.clientflow.backend.domain.staff.StaffProfileRepository;
import com.clientflow.backend.domain.stafftimeoff.dto.StaffTimeOffCreateRequest;
import com.clientflow.backend.domain.stafftimeoff.dto.StaffTimeOffResponse;
import com.clientflow.backend.domain.stafftimeoff.mapper.StaffTimeOffMapper;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffTimeOffService {

    StaffTimeOffRepository staffTimeOffRepository;
    BusinessRepository businessRepository;
    StaffProfileRepository staffProfileRepository;
    StaffTimeOffMapper staffTimeOffMapper;
    SecurityUtil securityUtil;

    @Transactional
    public StaffTimeOffResponse createTimeOff(
            Long businessId,
            Long staffId,
            StaffTimeOffCreateRequest request
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staff = getStaffProfile(business.getId(), staffId);

        validateTimeRange(request.startTime(), request.endTime());
        validateNoOverlap(staff, request);

        StaffTimeOff timeOff = StaffTimeOff.builder()
                .staffProfile(staff)
                .date(request.date())
                .startTime(request.startTime())
                .endTime(request.endTime())
                .reason(normalizeNullable(request.reason()))
                .build();

        return staffTimeOffMapper.toResponse(staffTimeOffRepository.save(timeOff));
    }

    @Transactional(readOnly = true)
    public PageResponse<StaffTimeOffResponse> getTimeOffs(
            Long businessId,
            Long staffId,
            Pageable pageable
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staff = getStaffProfile(business.getId(), staffId);

        return PageResponse.from(
                staffTimeOffRepository.findByStaffProfileId(staff.getId(), pageable)
                        .map(staffTimeOffMapper::toResponse)
        );
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new AppException(ErrorCode.INVALID_STAFF_TIME_OFF_RANGE);
        }
    }

    private void validateNoOverlap(StaffProfile staff, StaffTimeOffCreateRequest request) {
        boolean hasOverlap = staffTimeOffRepository
                .findByStaffProfileIdAndDate(staff.getId(), request.date())
                .stream()
                .anyMatch(existing ->
                        request.startTime().isBefore(existing.getEndTime())
                                && request.endTime().isAfter(existing.getStartTime())
                );

        if (hasOverlap) {
            throw new AppException(ErrorCode.STAFF_TIME_OFF_OVERLAP);
        }
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

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}