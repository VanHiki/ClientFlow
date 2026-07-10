package com.clientflow.backend.domain.appointmentnote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentNoteRepository extends JpaRepository<AppointmentNote, Long> {

    Page<AppointmentNote> findByAppointmentId(Long appointmentId, Pageable pageable);
}
