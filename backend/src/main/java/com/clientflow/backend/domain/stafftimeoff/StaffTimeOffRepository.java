package com.clientflow.backend.domain.stafftimeoff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StaffTimeOffRepository extends JpaRepository<StaffTimeOff, Long> {

    Page<StaffTimeOff> findByStaffProfileId(Long staffProfileId, Pageable pageable);

    List<StaffTimeOff> findByStaffProfileIdAndDate(Long staffProfileId, LocalDate date);

    Optional<StaffTimeOff> findByIdAndStaffProfileId(Long id, Long staffProfileId);
}
