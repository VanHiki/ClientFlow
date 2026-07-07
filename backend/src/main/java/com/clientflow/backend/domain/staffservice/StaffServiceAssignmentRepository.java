package com.clientflow.backend.domain.staffservice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StaffServiceAssignmentRepository extends JpaRepository<StaffServiceAssignment, Long> {
    boolean existsByStaffProfileIdAndServiceOfferingId(Long staffProfileId, Long serviceOfferingId);
    List<StaffServiceAssignment> findByServiceOfferingId(Long serviceOfferingId);
    Page<StaffServiceAssignment> findByStaffProfileId(Long staffProfileId, Pageable pageable);
}
