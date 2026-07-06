package com.clientflow.backend.domain.workinghour;


import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.staff.StaffProfile;
import com.clientflow.backend.domain.staff.StaffProfileRepository;
import com.clientflow.backend.domain.workinghour.dto.WorkingHourCreateRequest;
import com.clientflow.backend.domain.workinghour.dto.WorkingHourResponse;
import com.clientflow.backend.domain.workinghour.mapper.WorkingHourMapper;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WorkingHourService {
    WorkingHourRepository workingHourRepository;
    WorkingHourMapper  workingHourMapper;
    SecurityUtil securityUtil;
    StaffProfileRepository staffProfileRepository;
    BusinessRepository businessRepository;

    @Transactional
    public WorkingHourResponse createWorkingHour(Long businessId, Long staffId, WorkingHourCreateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staff = getStaffProfile(business.getId(), staffId);

        validateTimeRange(request.startTime(), request.endTime());

        validateNoOverlap(staff, request);

        WorkingHour workingHour = workingHourMapper.toEntity(request);
        workingHour.setStaffProfile(staff);
        workingHour.setActive(true);
        return workingHourMapper.toResponse(workingHourRepository.save(workingHour));
    }

    @Transactional(readOnly = true)
    public PageResponse<WorkingHourResponse> getWorkingHours(Long businessId, Long staffId, Pageable pageable) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staffProfile = getStaffProfile(business.getId(), staffId);

        return PageResponse.from(
                workingHourRepository.findByStaffProfileId(staffProfile.getId(), pageable)
                        .map(workingHourMapper::toResponse)
        );
    }


    // newStart < existingEnd && newEnd > existingStart\
    //cho phép ca liền nhau như 08:00-12:00 và 12:00-17:00, nhưng chặn 08:00-12:00 với 11:00-13:00.
    private void validateNoOverlap(StaffProfile staffProfile, WorkingHourCreateRequest request) {
        boolean hasOverlap = workingHourRepository
                .findByStaffProfileIdAndDayOfWeekAndActiveTrue(staffProfile.getId(), request.dayOfWeek())
                .stream()
                .anyMatch(existing ->
                        request.startTime().isBefore(existing.getEndTime())
                                && request.endTime().isAfter(existing.getStartTime())
                );

        if (hasOverlap) {
            throw new AppException(ErrorCode.WORKING_HOUR_OVERLAP);
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (!startTime.isBefore(endTime)) {
            throw new AppException(ErrorCode.INVALID_WORKING_HOUR_RANGE);
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


}
