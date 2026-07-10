package com.clientflow.backend.bootstrap;

import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.common.enums.RoleName;
import com.clientflow.backend.domain.appointment.Appointment;
import com.clientflow.backend.domain.appointment.AppointmentRepository;
import com.clientflow.backend.domain.appointment.BookingCodeService;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.business.BusinessRepository;
import com.clientflow.backend.domain.customer.Customer;
import com.clientflow.backend.domain.customer.CustomerRepository;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@Profile("demo")
@Order(2)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DemoDataInitializer implements CommandLineRunner {

    final RoleRepository roleRepository;
    final UserRepository userRepository;
    final BusinessRepository businessRepository;
    final ServiceOfferingRepository serviceOfferingRepository;
    final StaffProfileRepository staffProfileRepository;
    final StaffServiceAssignmentRepository staffServiceAssignmentRepository;
    final WorkingHourRepository workingHourRepository;
    final CustomerRepository customerRepository;
    final AppointmentRepository appointmentRepository;
    final BookingCodeService bookingCodeService;
    final PasswordEncoder passwordEncoder;

    @Value("${clientflow.demo.owner-email:owner@clientflow.local}")
    String ownerEmail;

    @Value("${clientflow.demo.owner-password:DemoOwner@123}")
    String ownerPassword;

    @Value("${clientflow.demo.staff-email:staff@clientflow.local}")
    String staffEmail;

    @Value("${clientflow.demo.staff-password:DemoStaff@123}")
    String staffPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (businessRepository.findBySlug("clientflow-demo").isPresent()) {
            return;
        }

        Role ownerRole = roleRepository.findByName(RoleName.OWNER).orElseThrow();
        Role staffRole = roleRepository.findByName(RoleName.STAFF).orElseThrow();

        User owner = userRepository.save(User.builder()
                .role(ownerRole)
                .fullName("ClientFlow Demo Owner")
                .email(ownerEmail.trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(ownerPassword))
                .phone("0909000000")
                .enabled(true)
                .build());

        Business business = businessRepository.save(Business.builder()
                .owner(owner)
                .name("ClientFlow Demo Salon")
                .slug("clientflow-demo")
                .phone("0909000000")
                .email("demo@clientflow.local")
                .address("Ho Chi Minh City")
                .timezone(BookingConstants.DEFAULT_TIMEZONE)
                .active(true)
                .build());

        ServiceOffering haircut = serviceOfferingRepository.save(ServiceOffering.builder()
                .business(business)
                .name("Hair Cut")
                .description("Professional haircut service")
                .price(new BigDecimal("150000"))
                .durationMinutes(30)
                .active(true)
                .build());

        ServiceOffering hairSpa = serviceOfferingRepository.save(ServiceOffering.builder()
                .business(business)
                .name("Hair Spa")
                .description("Hair and scalp care")
                .price(new BigDecimal("300000"))
                .durationMinutes(60)
                .active(true)
                .build());

        User staffUser = userRepository.save(User.builder()
                .role(staffRole)
                .fullName("Nguyen Demo Staff")
                .email(staffEmail.trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(staffPassword))
                .phone("0909000001")
                .enabled(true)
                .build());

        StaffProfile staff = staffProfileRepository.save(StaffProfile.builder()
                .business(business)
                .user(staffUser)
                .fullName(staffUser.getFullName())
                .email(staffUser.getEmail())
                .phone(staffUser.getPhone())
                .position("Senior stylist")
                .active(true)
                .build());

        staffServiceAssignmentRepository.saveAll(List.of(
                StaffServiceAssignment.builder()
                        .staffProfile(staff)
                        .serviceOffering(haircut)
                        .build(),
                StaffServiceAssignment.builder()
                        .staffProfile(staff)
                        .serviceOffering(hairSpa)
                        .build()
        ));

        List<DayOfWeek> workingDays = List.of(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY
        );

        workingHourRepository.saveAll(workingDays.stream()
                .map(day -> WorkingHour.builder()
                        .staffProfile(staff)
                        .dayOfWeek(day)
                        .startTime(LocalTime.of(8, 0))
                        .endTime(LocalTime.of(17, 0))
                        .active(true)
                        .build())
                .toList());

        Customer customer = customerRepository.save(Customer.builder()
                .business(business)
                .fullName("Tran Demo Customer")
                .phone("0909000002")
                .email("customer@clientflow.local")
                .notes("Demo customer")
                .active(true)
                .build());

        LocalDate today = LocalDate.now(BookingConstants.DEFAULT_ZONE_ID);
        appointmentRepository.saveAll(List.of(
                createAppointment(
                        business, customer, haircut, staff, today.minusDays(1),
                        LocalTime.of(10, 0), AppointmentStatus.COMPLETED
                ),
                createAppointment(
                        business, customer, haircut, staff, today.plusDays(1),
                        LocalTime.of(10, 0), AppointmentStatus.PENDING
                ),
                createAppointment(
                        business, customer, hairSpa, staff, today.plusDays(2),
                        LocalTime.of(14, 0), AppointmentStatus.CONFIRMED
                )
        ));
    }

    private Appointment createAppointment(
            Business business,
            Customer customer,
            ServiceOffering service,
            StaffProfile staff,
            LocalDate date,
            LocalTime startTime,
            AppointmentStatus status
    ) {
        return Appointment.builder()
                .bookingCode(bookingCodeService.generate(date))
                .business(business)
                .customer(customer)
                .serviceOffering(service)
                .staffProfile(staff)
                .appointmentDate(date)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(service.getDurationMinutes()))
                .status(status)
                .timezone(BookingConstants.DEFAULT_TIMEZONE)
                .note("Demo appointment")
                .build();
    }
}
