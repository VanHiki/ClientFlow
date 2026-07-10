package com.clientflow.backend.domain.publicbooking;

import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.appointment.Appointment;
import com.clientflow.backend.domain.appointment.AppointmentRepository;
import com.clientflow.backend.domain.appointment.AppointmentStatusTransitionPolicy;
import com.clientflow.backend.domain.appointment.BookingCodeService;
import com.clientflow.backend.domain.appointment.dto.AppointmentResponse;
import com.clientflow.backend.domain.appointment.mapper.AppointmentMapper;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.businessexception.BusinessExceptionDayRepository;
import com.clientflow.backend.domain.customer.Customer;
import com.clientflow.backend.domain.customer.CustomerRepository;
import com.clientflow.backend.domain.publicbooking.dto.PublicAppointmentCreateRequest;
import com.clientflow.backend.domain.publicbooking.dto.PublicBusinessResponse;
import com.clientflow.backend.domain.publicbooking.dto.PublicServiceResponse;
import com.clientflow.backend.domain.service.ServiceOffering;
import com.clientflow.backend.domain.service.ServiceOfferingRepository;
import com.clientflow.backend.domain.staff.StaffProfile;
import com.clientflow.backend.domain.staff.StaffProfileRepository;
import com.clientflow.backend.domain.staffservice.StaffServiceAssignmentRepository;
import com.clientflow.backend.domain.stafftimeoff.StaffTimeOffRepository;
import com.clientflow.backend.domain.workinghour.WorkingHourRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PublicBookingService {

    BusinessRepository businessRepository;
    ServiceOfferingRepository serviceOfferingRepository;
    CustomerRepository customerRepository;
    StaffProfileRepository staffProfileRepository;
    StaffServiceAssignmentRepository staffServiceAssignmentRepository;
    WorkingHourRepository workingHourRepository;
    StaffTimeOffRepository staffTimeOffRepository;
    BusinessExceptionDayRepository businessExceptionDayRepository;
    AppointmentRepository appointmentRepository;
    AppointmentMapper appointmentMapper;
    BookingCodeService bookingCodeService;

    @NonFinal
    @Value("${clientflow.booking.cancellation-notice-hours:2}")
    long cancellationNoticeHours;

    private static final List<AppointmentStatus> BLOCKING_STATUSES = List.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.CHECKED_IN,
            AppointmentStatus.COMPLETED
    );

    @Transactional(readOnly = true)
    public PublicBusinessResponse getBusiness(String slug) {
        Business business = getActiveBusiness(slug);

        return new PublicBusinessResponse(
                business.getId(),
                business.getName(),
                business.getSlug(),
                business.getPhone(),
                business.getEmail(),
                business.getAddress(),
                business.getTimezone()
        );
    }

    @Transactional(readOnly = true)
    public List<PublicServiceResponse> getServices(String slug) {
        Business business = getActiveBusiness(slug);

        return serviceOfferingRepository.findByBusinessIdAndActiveTrueOrderByCreatedAtDesc(business.getId())
                .stream()
                .map(service -> new PublicServiceResponse(
                        service.getId(),
                        service.getName(),
                        service.getDescription(),
                        service.getPrice(),
                        service.getDurationMinutes()
                ))
                .toList();
    }

    @Transactional
    public AppointmentResponse createAppointment(String slug, PublicAppointmentCreateRequest request) {
        Business business = getActiveBusiness(slug);

        Customer customer = findOrCreateCustomer(business, request);

        ServiceOffering service = serviceOfferingRepository.findByIdAndBusinessId(request.serviceId(), business.getId())
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));

        StaffProfile staff = staffProfileRepository.findByIdAndBusinessId(request.staffId(), business.getId())
                .orElseThrow(() -> new AppException(ErrorCode.STAFF_NOT_FOUND));

        if (!service.isActive()) {
            throw new AppException(ErrorCode.SERVICE_INACTIVE);
        }

        if (!staff.isActive()) {
            throw new AppException(ErrorCode.STAFF_INACTIVE);
        }

        if (!staffServiceAssignmentRepository.existsByStaffProfileIdAndServiceOfferingId(staff.getId(), service.getId())) {
            throw new AppException(ErrorCode.STAFF_NOT_ASSIGNED_TO_SERVICE);
        }

        LocalTime endTime = request.startTime().plusMinutes(service.getDurationMinutes());

        validateNotInPast(request);
        validateNotOnBusinessException(business, request);
        validateInsideWorkingHours(staff, request, endTime);
        validateNotDuringTimeOff(staff, request, endTime);
        validateNoOverlap(staff, request, endTime);

        Appointment appointment = Appointment.builder()
                .bookingCode(bookingCodeService.generate(request.appointmentDate()))
                .business(business)
                .customer(customer)
                .serviceOffering(service)
                .staffProfile(staff)
                .appointmentDate(request.appointmentDate())
                .startTime(request.startTime())
                .endTime(endTime)
                .status(AppointmentStatus.PENDING)
                .timezone(business.getTimezone())
                .note(normalizeNullable(request.note()))
                .build();

        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment(String slug, String bookingCode) {
        Business business = findBusinessBySlug(slug);
        Appointment appointment = getAppointmentByCode(business.getId(), bookingCode);

        return appointmentMapper.toResponse(appointment);
    }

    @Transactional
    public AppointmentResponse cancelAppointment(String slug, String bookingCode) {
        Business business = findBusinessBySlug(slug);
        Appointment appointment = getAppointmentByCode(business.getId(), bookingCode);

        if (appointment.getStatus() != AppointmentStatus.PENDING
                && appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new AppException(ErrorCode.PUBLIC_CANCELLATION_NOT_ALLOWED);
        }

        LocalDateTime appointmentStart = LocalDateTime.of(
                appointment.getAppointmentDate(),
                appointment.getStartTime()
        );
        LocalDateTime cancellationDeadline = appointmentStart.minusHours(cancellationNoticeHours);
        LocalDateTime now = LocalDateTime.now(BookingConstants.DEFAULT_ZONE_ID);

        if (now.isAfter(cancellationDeadline)) {
            throw new AppException(ErrorCode.PUBLIC_CANCELLATION_TOO_LATE);
        }

        AppointmentStatusTransitionPolicy.validate(appointment.getStatus(), AppointmentStatus.CANCELLED);
        appointment.setStatus(AppointmentStatus.CANCELLED);

        return appointmentMapper.toResponse(appointment);
    }

    private Customer findOrCreateCustomer(Business business, PublicAppointmentCreateRequest request) {
        String phone = request.customerPhone().trim();
        String email = normalizeEmail(request.customerEmail());

        return customerRepository.findByBusinessIdAndPhone(business.getId(), phone)
                .map(customer -> {
                    if (!customer.isActive()) {
                        customer.setActive(true);
                    }

                    return customer;
                })
                .orElseGet(() -> {
                    if (email != null && customerRepository.existsByBusinessIdAndEmailIgnoreCase(business.getId(), email)) {
                        throw new AppException(ErrorCode.CUSTOMER_EMAIL_ALREADY_EXISTS);
                    }

                    Customer customer = Customer.builder()
                            .business(business)
                            .fullName(request.customerFullName().trim())
                            .phone(phone)
                            .email(email)
                            .active(true)
                            .build();

                    return customerRepository.save(customer);
                });
    }



    private void validateNotInPast(PublicAppointmentCreateRequest request) {
        LocalDateTime appointmentStart = LocalDateTime.of(request.appointmentDate(), request.startTime());
        LocalDateTime now = LocalDateTime.now(BookingConstants.DEFAULT_ZONE_ID);

        if (appointmentStart.isBefore(now)) {
            throw new AppException(ErrorCode.APPOINTMENT_IN_PAST);
        }
    }

    private void validateNotOnBusinessException(Business business, PublicAppointmentCreateRequest request) {
        if (businessExceptionDayRepository.existsByBusinessIdAndDate(business.getId(), request.appointmentDate())) {
            throw new AppException(ErrorCode.APPOINTMENT_ON_BUSINESS_EXCEPTION);
        }
    }

    private void validateInsideWorkingHours(
            StaffProfile staff,
            PublicAppointmentCreateRequest request,
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
            PublicAppointmentCreateRequest request,
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

    private void validateNoOverlap(
            StaffProfile staff,
            PublicAppointmentCreateRequest request,
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

    private Business getActiveBusiness(String slug) {
        Business business = findBusinessBySlug(slug);

        if (!business.isActive()) {
            throw new AppException(ErrorCode.BUSINESS_NOT_FOUND);
        }

        return business;
    }

    private Business findBusinessBySlug(String slug) {
        return businessRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private Appointment getAppointmentByCode(Long businessId, String bookingCode) {
        return appointmentRepository.findByBusinessIdAndBookingCode(
                        businessId,
                        bookingCode.trim().toUpperCase()
                )
                .orElseThrow(() -> new AppException(ErrorCode.APPOINTMENT_NOT_FOUND));
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
