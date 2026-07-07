package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.entity.BaseEntity;
import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.customer.Customer;
import com.clientflow.backend.domain.service.ServiceOffering;
import com.clientflow.backend.domain.staff.StaffProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointments_business_date", columnList = "business_id, appointment_date"),
        @Index(name = "idx_appointments_staff_date", columnList = "staff_profile_id, appointment_date"),
        @Index(name = "idx_appointments_customer_id", columnList = "customer_id")
})
public class Appointment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceOffering serviceOffering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_profile_id", nullable = false)
    private StaffProfile staffProfile;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Builder.Default
    @Column(nullable = false, length = 80)
    private String timezone = BookingConstants.DEFAULT_TIMEZONE;

    @Column(columnDefinition = "TEXT")
    private String note;
}