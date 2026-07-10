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
import com.clientflow.backend.domain.dashboard.dto.TopServiceResponse;
import com.clientflow.backend.domain.service.ServiceOfferingRepository;
import com.clientflow.backend.domain.staff.StaffProfileRepository;
import com.clientflow.backend.security.SecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.math.BigDecimal;
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

    private static final List<AppointmentStatus> BOOKED_STATUSES = List.of(
            AppointmentStatus.PENDING,
            AppointmentStatus.CONFIRMED,
            AppointmentStatus.CHECKED_IN,
            AppointmentStatus.COMPLETED
    );

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long businessId, LocalDate fromDate, LocalDate toDate) {
        Business business = getCurrentOwnerBusiness(businessId);
        LocalDate today = LocalDate.now(BookingConstants.DEFAULT_ZONE_ID);
        validateDateRange(fromDate, toDate);

        List<UpcomingAppointmentResponse> upcomingAppointments = appointmentRepository
                .findTop5ByBusinessIdAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscStartTimeAsc(
                        business.getId(),
                        today,
                        UPCOMING_STATUSES
                )
                .stream()
                .map(this::toUpcomingAppointmentResponse)
                .toList();

        List<TopServiceResponse> topServices = appointmentRepository.findTopServicesForDashboard(
                        business.getId(),
                        BOOKED_STATUSES,
                        fromDate,
                        toDate,
                        PageRequest.of(0, 5)
                )
                .stream()
                .map(projection -> new TopServiceResponse(
                        projection.getServiceId(),
                        projection.getServiceName(),
                        projection.getBookingCount()
                ))
                .toList();

        BigDecimal completedRevenue = appointmentRepository.sumRevenueForDashboard(
                business.getId(),
                AppointmentStatus.COMPLETED,
                fromDate,
                toDate
        );

        return new DashboardResponse(
                customerRepository.countByBusinessIdAndActiveTrue(business.getId()),
                serviceOfferingRepository.countByBusinessIdAndActiveTrue(business.getId()),
                staffProfileRepository.countByBusinessIdAndActiveTrue(business.getId()),
                fromDate,
                toDate,
                appointmentRepository.countForDashboard(business.getId(), null, fromDate, toDate),
                appointmentRepository.countByBusinessIdAndAppointmentDate(business.getId(), today),
                appointmentRepository.countForDashboard(
                        business.getId(), AppointmentStatus.PENDING, fromDate, toDate
                ),
                appointmentRepository.countForDashboard(
                        business.getId(), AppointmentStatus.CONFIRMED, fromDate, toDate
                ),
                appointmentRepository.countForDashboard(
                        business.getId(), AppointmentStatus.COMPLETED, fromDate, toDate
                ),
                appointmentRepository.countForDashboard(
                        business.getId(), AppointmentStatus.CANCELLED, fromDate, toDate
                ),
                completedRevenue,
                topServices,
                upcomingAppointments
        );
    }

    private void validateDateRange(LocalDate fromDate, LocalDate toDate) {
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }
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
