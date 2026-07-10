package com.clientflow.backend.domain.workinghour;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkingHourRepository extends JpaRepository<WorkingHour, Long> {

    Page<WorkingHour> findByStaffProfileId(Long staffProfileId, Pageable pageable);

    Optional<WorkingHour> findByIdAndStaffProfileId(Long id, Long staffProfileId);

    List<WorkingHour> findByStaffProfileIdAndDayOfWeekAndActiveTrue(
            Long staffProfileId,
            DayOfWeek dayOfWeek
    );
}
