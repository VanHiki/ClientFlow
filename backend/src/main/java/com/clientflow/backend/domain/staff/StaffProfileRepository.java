package com.clientflow.backend.domain.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {

    Page<StaffProfile> findByBusinessId(Long businessId, Pageable pageable);

    Optional<StaffProfile> findByIdAndBusinessId(Long id, Long businessId);

    Optional<StaffProfile> findByUserId(Long userId);

    boolean existsByBusinessIdAndEmailIgnoreCase(Long businessId, String email);

    boolean existsByBusinessIdAndEmailIgnoreCaseAndIdNot(Long businessId, String email, Long id);

    long countByBusinessIdAndActiveTrue(Long businessId);
}
