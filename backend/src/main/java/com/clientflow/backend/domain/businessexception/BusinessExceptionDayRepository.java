package com.clientflow.backend.domain.businessexception;

import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessExceptionDayRepository extends JpaRepository<BusinessExceptionDay, Long> {

    Page<BusinessExceptionDay> findByBusinessId(Long businessId, Pageable pageable);

    Optional<BusinessExceptionDay> findByIdAndBusinessId(Long id, Long businessId);

    boolean existsByBusinessIdAndDate(Long businessId, LocalDate date);
}
