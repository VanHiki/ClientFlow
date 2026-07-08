package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.common.response.PageResponse;
import com.clientflow.backend.domain.appointment.dto.AppointmentCreateRequest;
import com.clientflow.backend.domain.appointment.dto.AppointmentResponse;
import com.clientflow.backend.domain.appointment.dto.AppointmentStatusUpdateRequest;
import com.clientflow.backend.domain.appointment.mapper.AppointmentMapper;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.customer.Customer;
import com.clientflow.backend.domain.customer.CustomerRepository;
import com.clientflow.backend.domain.service.ServiceOffering;
import com.clientflow.backend.domain.service.ServiceOfferingRepository;
import com.clientflow.backend.domain.staff.StaffProfile;
import com.clientflow.backend.domain.staff.StaffProfileRepository;
import com.clientflow.backend.domain.staffservice.StaffServiceAssignmentRepository;
import com.clientflow.backend.domain.stafftimeoff.StaffTimeOffRepository;
import com.clientflow.backend.domain.workinghour.WorkingHourRepository;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentService {
    AppointmentRepository appointmentRepository;
    BusinessRepository businessRepository;
    CustomerRepository customerRepository;
    ServiceOfferingRepository serviceOfferingRepository;
    StaffProfileRepository staffProfileRepository;
    StaffServiceAssignmentRepository staffServiceAssignmentRepository;
    WorkingHourRepository workingHourRepository;
    StaffTimeOffRepository staffTimeOffRepository;
    AppointmentMapper appointmentMapper;
    SecurityUtil securityUtil;

    private static final List<AppointmentStatus> BLOCKING_STATUSES = List.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.CHECKED_IN,
            AppointmentStatus.COMPLETED
    );


    @Transactional
    public AppointmentResponse createAppointment(Long businessId, AppointmentCreateRequest request) {
        Business business = getCurrentOwnerBusiness(businessId);

        Customer customer = customerRepository.findByIdAndBusinessId(request.customerId(), business.getId())
                .orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

        ServiceOffering service = serviceOfferingRepository.findByIdAndBusinessId(request.serviceId(), business.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        StaffProfile staff = staffProfileRepository.findByIdAndBusinessId(request.staffId(), business.getId())
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

        if (!service.isActive()) {
            throw new AppException(ErrorCode.SERVICE_INACTIVE);
        }

        if (!staffServiceAssignmentRepository.existsByStaffProfileIdAndServiceOfferingId(staff.getId(), service.getId())) {
            throw new AppException(ErrorCode.STAFF_NOT_ASSIGNED_TO_SERVICE);
        }

        LocalTime endTime = request.startTime().plusMinutes(service.getDurationMinutes());

        validateNotInPast(request);
        validateInsideWorkingHours(staff, request, endTime);
        validateNotDuringTimeOff(staff, request, endTime);
        validateNoOverlap(staff, request, endTime);

        Appointment appointment = Appointment.builder()
                .business(business)
                .customer(customer)
                .serviceOffering(service)
                .staffProfile(staff)
                .appointmentDate(request.appointmentDate())
                .startTime(request.startTime())
                .endTime(endTime)
                .status(AppointmentStatus.PENDING)
                .timezone(BookingConstants.DEFAULT_TIMEZONE)
                .note(normalizeNullable(request.note()))
                .build();

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }


    @Transactional(readOnly = true)
    public PageResponse<AppointmentResponse> getAppointments(Long businessId, Pageable pageable) {
        Business business = getCurrentOwnerBusiness(businessId);

        return PageResponse.from(
                appointmentRepository.findByBusinessId(business.getId(), pageable)
                        .map(appointmentMapper::toResponse)
        );
    }

    @Transactional
    public AppointmentResponse updateAppointmentStatus(
            Long businessId,
            Long appointmentId,
            AppointmentStatusUpdateRequest request
    ) {
        Business business = getCurrentOwnerBusiness(businessId);

        Appointment appointment = appointmentRepository.findByIdAndBusinessId(appointmentId, business.getId())
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));

        validateStatusTransition(appointment.getStatus(), request.status());

        appointment.setStatus(request.status());

        return appointmentMapper.toResponse(appointment);
    }

    private void validateStatusTransition(AppointmentStatus currentStatus, AppointmentStatus nextStatus) {
        if (currentStatus == nextStatus) {
            return;
        }

        boolean valid = switch (currentStatus) {
            case PENDING -> nextStatus == AppointmentStatus.CONFIRMED
                    || nextStatus == AppointmentStatus.CANCELLED;

            case CONFIRMED -> nextStatus == AppointmentStatus.CHECKED_IN
                    || nextStatus == AppointmentStatus.COMPLETED
                    || nextStatus == AppointmentStatus.CANCELLED
                    || nextStatus == AppointmentStatus.NO_SHOW;

            case CHECKED_IN -> nextStatus == AppointmentStatus.COMPLETED
                    || nextStatus == AppointmentStatus.CANCELLED;

            case COMPLETED, CANCELLED, NO_SHOW -> false;
        };

        if (!valid) {
            throw new AppException(ErrorCode.INVALID_APPOINTMENT_STATUS_TRANSITION);
        }
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private void validateNoOverlap(
            StaffProfile staff,
            AppointmentCreateRequest request,
            LocalTime endTime
    ) {
        boolean hasOverlap = appointmentRepository
                .existsByStaffProfileIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
                        staff.getId(),
                        request.appointmentDate(),
                        BLOCKING_STATUSES,
                        endTime,
                        request.startTime()
                );

        if (hasOverlap) {
            throw new AppException(ErrorCode.APPOINTMENT_OVERLAP);
        }
    }

    private void validateNotInPast(AppointmentCreateRequest request) {
        LocalDateTime appointmentStart = LocalDateTime.of(request.appointmentDate(), request.startTime());
        LocalDateTime now = LocalDateTime.now(BookingConstants.DEFAULT_ZONE_ID);

        if (appointmentStart.isBefore(now)) {
            throw new AppException(ErrorCode.APPOINTMENT_IN_PAST);
        }
    }

    private void validateInsideWorkingHours(
            StaffProfile staff,
            AppointmentCreateRequest request,
            LocalTime endTime
    ) {
        boolean insideWorkingHours = workingHourRepository
                .findByStaffProfileIdAndDayOfWeekAndActiveTrue(
                        staff.getId(),
                        request.appointmentDate().getDayOfWeek()
                )
                .stream()
                .anyMatch(workingHour ->
                        !request.startTime().isBefore(workingHour.getStartTime())
                                && !endTime.isAfter(workingHour.getEndTime())
                );

        if (!insideWorkingHours) {
            throw new AppException(ErrorCode.APPOINTMENT_OUTSIDE_WORKING_HOURS);
        }
    }

    private void validateNotDuringTimeOff(
            StaffProfile staff,
            AppointmentCreateRequest request,
            LocalTime endTime
    ) {
        boolean duringTimeOff = staffTimeOffRepository
                .findByStaffProfileIdAndDate(staff.getId(), request.appointmentDate())
                .stream()
                .anyMatch(timeOff ->
                        request.startTime().isBefore(timeOff.getEndTime())
                                && endTime.isAfter(timeOff.getStartTime())
                );

        if (duringTimeOff) {
            throw new AppException(ErrorCode.APPOINTMENT_DURING_STAFF_TIME_OFF);
        }
    }
}
