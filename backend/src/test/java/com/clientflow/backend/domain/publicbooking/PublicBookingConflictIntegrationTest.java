package com.clientflow.backend.domain.publicbooking;

import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.ErrorCode;
import com.clientflow.backend.common.enums.RoleName;
import com.clientflow.backend.common.exception.AppException;
import com.clientflow.backend.domain.appointment.dto.AppointmentResponse;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.publicbooking.dto.PublicAppointmentCreateRequest;
import com.clientflow.backend.domain.role.Role;
import com.clientflow.backend.domain.role.RoleRepository;
import com.clientflow.backend.domain.service.ServiceOffering;
import com.clientflow.backend.domain.service.ServiceOfferingRepository;
import com.clientflow.backend.domain.staff.StaffProfile;
import com.clientflow.backend.domain.staff.StaffProfileRepository;
import com.clientflow.backend.domain.staffservice.StaffServiceAssignment;
import com.clientflow.backend.domain.staffservice.StaffServiceAssignmentRepository;
import com.clientflow.backend.domain.user.User;
import com.clientflow.backend.domain.user.UserRepository;
import com.clientflow.backend.domain.workinghour.WorkingHour;
import com.clientflow.backend.domain.workinghour.WorkingHourRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PublicBookingConflictIntegrationTest {

    @Autowired
    PublicBookingService publicBookingService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BusinessRepository businessRepository;

    @Autowired
    ServiceOfferingRepository serviceOfferingRepository;

    @Autowired
    StaffProfileRepository staffProfileRepository;

    @Autowired
    StaffServiceAssignmentRepository staffServiceAssignmentRepository;

    @Autowired
    WorkingHourRepository workingHourRepository;

    @Test
    void rejectsSecondBookingForSameStaffAndTimeRange() {
        LocalDate appointmentDate = LocalDate.now(BookingConstants.DEFAULT_ZONE_ID).plusDays(7);
        Role ownerRole = roleRepository.findByName(RoleName.OWNER).orElseThrow();

        User owner = userRepository.save(User.builder()
                .role(ownerRole)
                .fullName("Test Owner")
                .email("owner-conflict-test@example.com")
                .passwordHash("not-used")
                .enabled(true)
                .build());

        Business business = businessRepository.save(Business.builder()
                .owner(owner)
                .name("Conflict Test Business")
                .slug("conflict-test-business")
                .active(true)
                .build());

        ServiceOffering service = serviceOfferingRepository.save(ServiceOffering.builder()
                .business(business)
                .name("Conflict Test Service")
                .price(new BigDecimal("100000"))
                .durationMinutes(60)
                .active(true)
                .build());

        StaffProfile staff = staffProfileRepository.save(StaffProfile.builder()
                .business(business)
                .fullName("Conflict Test Staff")
                .active(true)
                .build());

        staffServiceAssignmentRepository.save(StaffServiceAssignment.builder()
                .staffProfile(staff)
                .serviceOffering(service)
                .build());

        workingHourRepository.save(WorkingHour.builder()
                .staffProfile(staff)
                .dayOfWeek(appointmentDate.getDayOfWeek())
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(17, 0))
                .active(true)
                .build());

        PublicAppointmentCreateRequest request = new PublicAppointmentCreateRequest(
                "Conflict Test Customer",
                "0909000999",
                "conflict-customer@example.com",
                service.getId(),
                staff.getId(),
                appointmentDate,
                LocalTime.of(9, 0),
                "Conflict test"
        );

        AppointmentResponse firstAppointment = publicBookingService.createAppointment(business.getSlug(), request);

        assertEquals(AppointmentStatus.PENDING, firstAppointment.status());
        assertNotNull(firstAppointment.bookingCode());

        AppException exception = assertThrows(
                AppException.class,
                () -> publicBookingService.createAppointment(business.getSlug(), request)
        );

        assertEquals(ErrorCode.APPOINTMENT_OVERLAP, exception.getErrorCode());
    }
}
