package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.enums.AppointmentStatus;
import com.clientflow.backend.domain.dashboard.TopServiceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByBusinessId(Long businessId, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "serviceOffering", "staffProfile"})
    @Query("""
            select appointment
            from Appointment appointment
            where appointment.business.id = :businessId
              and (:status is null or appointment.status = :status)
              and (:staffId is null or appointment.staffProfile.id = :staffId)
              and (:customerId is null or appointment.customer.id = :customerId)
              and (:fromDate is null or appointment.appointmentDate >= :fromDate)
              and (:toDate is null or appointment.appointmentDate <= :toDate)
            """)
    Page<Appointment> search(
            @Param("businessId") Long businessId,
            @Param("status") AppointmentStatus status,
            @Param("staffId") Long staffId,
            @Param("customerId") Long customerId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    List<Appointment> findByStaffProfileIdAndAppointmentDateAndStatusIn(
            Long staffProfileId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses
    );

    boolean existsByStaffProfileIdAndAppointmentDateAndStatusInAndStartTimeLessThanAndEndTimeGreaterThan(
            Long staffProfileId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses,
            LocalTime endTime,
            LocalTime startTime
    );

    Optional<Appointment> findByIdAndBusinessId(Long id, Long businessId);

    Optional<Appointment> findByIdAndStaffProfileId(Long id, Long staffProfileId);

    Optional<Appointment> findByBusinessIdAndBookingCode(Long businessId, String bookingCode);

    boolean existsByBookingCode(String bookingCode);

    long countByBusinessId(Long businessId);

    long countByBusinessIdAndAppointmentDate(Long businessId, LocalDate appointmentDate);

    long countByBusinessIdAndStatus(Long businessId, AppointmentStatus status);

    @Query("""
            select count(appointment)
            from Appointment appointment
            where appointment.business.id = :businessId
              and (:status is null or appointment.status = :status)
              and (:fromDate is null or appointment.appointmentDate >= :fromDate)
              and (:toDate is null or appointment.appointmentDate <= :toDate)
            """)
    long countForDashboard(
            @Param("businessId") Long businessId,
            @Param("status") AppointmentStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            select coalesce(sum(appointment.serviceOffering.price), 0)
            from Appointment appointment
            where appointment.business.id = :businessId
              and appointment.status = :status
              and (:fromDate is null or appointment.appointmentDate >= :fromDate)
              and (:toDate is null or appointment.appointmentDate <= :toDate)
            """)
    BigDecimal sumRevenueForDashboard(
            @Param("businessId") Long businessId,
            @Param("status") AppointmentStatus status,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("""
            select appointment.serviceOffering.id as serviceId,
                   appointment.serviceOffering.name as serviceName,
                   count(appointment) as bookingCount
            from Appointment appointment
            where appointment.business.id = :businessId
              and appointment.status in :statuses
              and (:fromDate is null or appointment.appointmentDate >= :fromDate)
              and (:toDate is null or appointment.appointmentDate <= :toDate)
            group by appointment.serviceOffering.id, appointment.serviceOffering.name
            order by count(appointment) desc, appointment.serviceOffering.name asc
            """)
    List<TopServiceProjection> findTopServicesForDashboard(
            @Param("businessId") Long businessId,
            @Param("statuses") Collection<AppointmentStatus> statuses,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"customer", "serviceOffering", "staffProfile"})
    List<Appointment> findTop5ByBusinessIdAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscStartTimeAsc(
            Long businessId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses
    );
}
