package com.clientflow.backend.domain.availability;

import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.appointment.Appointment;
import com.clientflow.backend.domain.appointment.AppointmentRepository;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.availability.dto.AvailableSlotResponse;
import com.clientflow.backend.domain.businessexception.BusinessExceptionDayRepository;
import com.clientflow.backend.domain.service.ServiceOffering;
import com.clientflow.backend.domain.service.ServiceOfferingRepository;
import com.clientflow.backend.domain.staff.StaffProfile;
import com.clientflow.backend.domain.staffservice.StaffServiceAssignment;
import com.clientflow.backend.domain.staffservice.StaffServiceAssignmentRepository;
import com.clientflow.backend.domain.stafftimeoff.StaffTimeOff;
import com.clientflow.backend.domain.stafftimeoff.StaffTimeOffRepository;
import com.clientflow.backend.domain.workinghour.WorkingHour;
import com.clientflow.backend.domain.workinghour.WorkingHourRepository;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AvailabilityService {

    BusinessRepository businessRepository;
    ServiceOfferingRepository serviceOfferingRepository;
    StaffServiceAssignmentRepository staffServiceAssignmentRepository;
    WorkingHourRepository workingHourRepository;
    AppointmentRepository appointmentRepository;
    StaffTimeOffRepository staffTimeOffRepository;
    BusinessExceptionDayRepository businessExceptionDayRepository;
    SecurityUtil securityUtil;

    private static final List<AppointmentStatus> BLOCKING_STATUSES = List.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.CHECKED_IN,
            AppointmentStatus.COMPLETED
    );

    @Transactional(readOnly = true)
    public List<AvailableSlotResponse> getPublicAvailableSlots(String slug, Long serviceId, LocalDate date) {
        Business business = businessRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));

        if (!business.isActive()) {
            throw new AppException(ErrorCode.BUSINESS_NOT_FOUND);
        }

        ServiceOffering service = serviceOfferingRepository.findByIdAndBusinessId(serviceId, business.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if (!service.isActive()) {
            throw new AppException(ErrorCode.SERVICE_INACTIVE);
        }

        return buildAvailableSlots(business, service, date);
    }

    @Transactional(readOnly = true)
    public List<AvailableSlotResponse> getAvailableSlots(Long businessId, Long serviceId, LocalDate date) {
        Business business = getCurrentOwnerBusiness(businessId);

        ServiceOffering service = serviceOfferingRepository.findByIdAndBusinessId(serviceId, business.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        if (!service.isActive()) {
            throw new AppException(ErrorCode.SERVICE_INACTIVE);
        }

        return buildAvailableSlots(business, service, date);
    }

    private List<AvailableSlotResponse> buildAvailableSlots(Business business, ServiceOffering service, LocalDate date) {
        List<AvailableSlotResponse> slots = new ArrayList<>();

        if (businessExceptionDayRepository.existsByBusinessIdAndDate(business.getId(), date)) {
            return slots;
        }

        List<StaffServiceAssignment> assignments =
                staffServiceAssignmentRepository.findByServiceOfferingId(service.getId());

        for (StaffServiceAssignment assignment : assignments) {
            StaffProfile staff = assignment.getStaffProfile();

            if (!staff.isActive()) {
                continue;
            }

            List<WorkingHour> workingHours =
                    workingHourRepository.findByStaffProfileIdAndDayOfWeekAndActiveTrue(
                            staff.getId(),
                            date.getDayOfWeek()
                    );

            List<Appointment> appointments =
                    appointmentRepository.findByStaffProfileIdAndAppointmentDateAndStatusIn(
                            staff.getId(),
                            date,
                            BLOCKING_STATUSES
                    );

            List<StaffTimeOff> timeOffs =
                    staffTimeOffRepository.findByStaffProfileIdAndDate(staff.getId(), date);

            for (WorkingHour workingHour : workingHours) {
                slots.addAll(buildSlotsForWorkingHour(staff, service, date, workingHour, appointments, timeOffs));
            }
        }

        return slots;
    }

    private List<AvailableSlotResponse> buildSlotsForWorkingHour(
            StaffProfile staff,
            ServiceOffering service,
            LocalDate date,
            WorkingHour workingHour,
            List<Appointment> appointments,
            List<StaffTimeOff> timeOffs
    ) {
        List<AvailableSlotResponse> slots = new ArrayList<>();

        LocalTime slotStart = workingHour.getStartTime();
        LocalTime workingEnd = workingHour.getEndTime();

        while (!slotStart.plusMinutes(service.getDurationMinutes()).isAfter(workingEnd)) {
            LocalTime slotEnd = slotStart.plusMinutes(service.getDurationMinutes());

            if (!isPastSlot(date, slotStart)
                    && !hasAppointmentOverlap(slotStart, slotEnd, appointments)
                    && !hasTimeOffOverlap(slotStart, slotEnd, timeOffs)) {
                slots.add(new AvailableSlotResponse(
                        staff.getId(),
                        staff.getFullName(),
                        date,
                        slotStart,
                        slotEnd
                ));
            }

            slotStart = slotStart.plusMinutes(BookingConstants.SLOT_STEP_MINUTES);
        }

        return slots;
    }

    private boolean isPastSlot(LocalDate date, LocalTime startTime) {
        return LocalDateTime.of(date, startTime)
                .isBefore(LocalDateTime.now(BookingConstants.DEFAULT_ZONE_ID));
    }

    private boolean hasAppointmentOverlap(LocalTime slotStart, LocalTime slotEnd, List<Appointment> appointments) {
        return appointments.stream()
                .anyMatch(appointment ->
                        slotStart.isBefore(appointment.getEndTime())
                                && slotEnd.isAfter(appointment.getStartTime())
                );
    }

    private boolean hasTimeOffOverlap(LocalTime slotStart, LocalTime slotEnd, List<StaffTimeOff> timeOffs) {
        return timeOffs.stream()
                .anyMatch(timeOff ->
                        slotStart.isBefore(timeOff.getEndTime())
                                && slotEnd.isAfter(timeOff.getStartTime())
                );
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }
}
