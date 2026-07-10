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
import com.clientflow.backend.domain.workinghour.dto.WorkingHourStatusUpdateRequest;
import com.clientflow.backend.domain.workinghour.dto.WorkingHourUpdateRequest;
import com.clientflow.backend.domain.workinghour.mapper.WorkingHourMapper;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class WorkingHourService {
    WorkingHourRepository workingHourRepository;
    WorkingHourMapper workingHourMapper;
    SecurityUtil securityUtil;
    StaffProfileRepository staffProfileRepository;
    BusinessRepository businessRepository;

    @Transactional
    public WorkingHourResponse createWorkingHour(Long businessId, Long staffId, WorkingHourCreateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staff = getStaffProfile(business.getId(), staffId);

        validateTimeRange(request.startTime(), request.endTime());

        validateNoOverlap(staff, request.dayOfWeek(), request.startTime(), request.endTime(), null);

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

    @Transactional
    public WorkingHourResponse updateWorkingHour(
            Long businessId,
            Long staffId,
            Long workingHourId,
            WorkingHourUpdateRequest request
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staff = getStaffProfile(business.getId(), staffId);
        WorkingHour workingHour = getWorkingHour(staff.getId(), workingHourId);

        validateTimeRange(request.startTime(), request.endTime());

        if (workingHour.isActive()) {
            validateNoOverlap(
                    staff,
                    request.dayOfWeek(),
                    request.startTime(),
                    request.endTime(),
                    workingHour.getId()
            );
        }

        workingHour.setDayOfWeek(request.dayOfWeek());
        workingHour.setStartTime(request.startTime());
        workingHour.setEndTime(request.endTime());

        return workingHourMapper.toResponse(workingHour);
    }

    @Transactional
    public WorkingHourResponse updateWorkingHourStatus(
            Long businessId,
            Long staffId,
            Long workingHourId,
            WorkingHourStatusUpdateRequest request
    ) {
        Business business = getCurrentOwnerBusiness(businessId);
        StaffProfile staff = getStaffProfile(business.getId(), staffId);
        WorkingHour workingHour = getWorkingHour(staff.getId(), workingHourId);

        if (request.active()) {
            validateNoOverlap(
                    staff,
                    workingHour.getDayOfWeek(),
                    workingHour.getStartTime(),
                    workingHour.getEndTime(),
                    workingHour.getId()
            );
        }

        workingHour.setActive(request.active());

        return workingHourMapper.toResponse(workingHour);
    }

    private void validateNoOverlap(
            StaffProfile staffProfile,
            DayOfWeek dayOfWeek,
            LocalTime startTime,
            LocalTime endTime,
            Long excludeWorkingHourId
    ) {
        boolean hasOverlap = workingHourRepository
                .findByStaffProfileIdAndDayOfWeekAndActiveTrue(staffProfile.getId(), dayOfWeek)
                .stream()
                .anyMatch(existing ->
                        !existing.getId().equals(excludeWorkingHourId)
                                && startTime.isBefore(existing.getEndTime())
                                && endTime.isAfter(existing.getStartTime())
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

    private WorkingHour getWorkingHour(Long staffId, Long workingHourId) {
        return workingHourRepository.findByIdAndStaffProfileId(workingHourId, staffId)
                .orElseThrow(() -> new AppException(ErrorCode.WORKING_HOUR_NOT_FOUND));
    }

}
