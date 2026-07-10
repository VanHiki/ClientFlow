package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
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

    long countByBusinessId(Long businessId);

    long countByBusinessIdAndAppointmentDate(Long businessId, LocalDate appointmentDate);

    long countByBusinessIdAndStatus(Long businessId, AppointmentStatus status);

    List<Appointment> findTop5ByBusinessIdAndAppointmentDateGreaterThanEqualAndStatusInOrderByAppointmentDateAscStartTimeAsc(
            Long businessId,
            LocalDate appointmentDate,
            Collection<AppointmentStatus> statuses
    );
}
