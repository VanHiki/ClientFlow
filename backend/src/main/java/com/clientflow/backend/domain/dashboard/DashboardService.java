package com.clientflow.backend.domain.dashboard;

import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.appointment.Appointment;
import com.clientflow.backend.domain.appointment.AppointmentRepository;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.customer.CustomerRepository;
import com.clientflow.backend.domain.dashboard.dto.DashboardResponse;
import com.clientflow.backend.domain.dashboard.dto.UpcomingAppointmentResponse;
import com.clientflow.backend.domain.service.ServiceOfferingRepository;
import com.clientflow.backend.domain.staff.StaffProfileRepository;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DashboardService {

    BusinessRepository businessRepository;
    CustomerRepository customerRepository;
    ServiceOfferingRepository serviceOfferingRepository;
    StaffProfileRepository staffProfileRepository;
    AppointmentRepository appointmentRepository;
    SecurityUtil securityUtil;

    private static final List<AppointmentStatus> UPCOMING_STATUSES = List.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.CHECKED_IN
    );

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long businessId) {
        Business business = getCurrentOwnerBusiness(businessId);
        LocalDate today = LocalDate.now(BookingConstants.DEFAULT_ZONE_ID);

        List<UpcomingAppointmentResponse> upcomingAppointments = appointmentRepository
                .findTop5ByBusinessIdAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscStartTimeAsc(
                        business.getId(),
                        today,
                        UPCOMING_STATUSES
                )
                .stream()
                .map(this::toUpcomingAppointmentResponse)
                .toList();

        return new DashboardResponse(
                customerRepository.countByBusinessIdAndActiveTrue(business.getId()),
                serviceOfferingRepository.countByBusinessIdAndActiveTrue(business.getId()),
                staffProfileRepository.countByBusinessIdAndActiveTrue(business.getId()),
                appointmentRepository.countByBusinessId(business.getId()),
                appointmentRepository.countByBusinessIdAndAppointmentDate(business.getId(), today),
                appointmentRepository.countByBusinessIdAndStatus(business.getId(), AppointmentStatus.PENDING),
                appointmentRepository.countByBusinessIdAndStatus(business.getId(), AppointmentStatus.CONFIRMED),
                appointmentRepository.countByBusinessIdAndStatus(business.getId(), AppointmentStatus.COMPLETED),
                upcomingAppointments
        );
    }

    private UpcomingAppointmentResponse toUpcomingAppointmentResponse(Appointment appointment) {
        return new UpcomingAppointmentResponse(
                appointment.getId(),
                appointment.getCustomer().getFullName(),
                appointment.getServiceOffering().getName(),
                appointment.getStaffProfile().getFullName(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getStatus()
        );
    }

    private Business getCurrentOwnerBusiness(Long businessId) {
        Long ownerId = securityUtil.getCurrentUserId();

        return businessRepository.findByIdAndOwnerId(businessId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.BUSINESS_NOT_FOUND));
    }
}