package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.appointment.dto.AppointmentResponse;
import com.clientflow.backend.domain.appointment.dto.AppointmentStatusUpdateRequest;
import com.clientflow.backend.domain.appointment.mapper.AppointmentMapper;
import com.clientflow.backend.domain.staff.StaffProfile;
import com.clientflow.backend.domain.staff.StaffProfileRepository;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StaffAppointmentService {

    AppointmentRepository appointmentRepository;
    StaffProfileRepository staffProfileRepository;
    AppointmentMapper appointmentMapper;
    SecurityUtil securityUtil;

    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAppointments(
            AppointmentStatus status,
            LocalDate fromDate,
            LocalDate toDate,
            Pageable pageable
    ) {
        StaffProfile staff = getCurrentStaff();
        validateDateRange(fromDate, toDate);

        return PageResponse.from(
                appointmentRepository.search(
                                staff.getBusiness().getId(),
                                status,
                                staff.getId(),
                                null,
                                fromDate,
                                toDate,
                                pageable
                        )
                        .map(appointmentMapper::toResponse)
        );
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment(Long appointmentId) {
        StaffProfile staff = getCurrentStaff();
        Appointment appointment = getAssignedAppointment(staff.getId(), appointmentId);

        return appointmentMapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse updateAppointmentStatus(
            Long appointmentId,
            AppointmentStatusUpdateRequest request
    ) {
        StaffProfile staff = getCurrentStaff();
        Appointment appointment = getAssignedAppointment(staff.getId(), appointmentId);

        AppointmentStatusTransitionPolicy.validate(appointment.getStatus(), request.status());
        appointment.setStatus(request.status());

        return appointmentMapper.toResponse(appointment);
    }

    private StaffProfile getCurrentStaff() {
        StaffProfile staff = staffProfileRepository.findByUserId(securityUtil.getCurrentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

        if (!staff.isActive()) {
            throw new AppException(ErrorCode.STAFF_INACTIVE);
        }

        return staff;
    }

    private Appointment getAssignedAppointment(Long staffId, Long appointmentId) {
        return appointmentRepository.findByIdAndStaffProfileId(appointmentId, staffId)
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }
    }
}
