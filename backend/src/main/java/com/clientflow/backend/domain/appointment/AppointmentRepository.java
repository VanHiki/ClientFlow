package com.clientflow.backend.domain.appointment;

import com.clientflow.backend.common.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByBusinessId(Long businessId, Pageable pageable);

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
}