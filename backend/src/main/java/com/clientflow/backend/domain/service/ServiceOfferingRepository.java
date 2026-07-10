package com.clientflow.backend.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {

    Page<ServiceOffering> findByBusinessId(Long businessId, Pageable pageable);
    Optional<ServiceOffering> findByIdAndBusinessId(Long id, Long businessId);
    List<ServiceOffering> findByBusinessIdAndActiveTrueOrderByCreatedAtDesc(Long businessId);
    boolean existsByBusinessIdAndNameIgnoreCase(Long businessId, String name);
    boolean existsByBusinessIdAndNameIgnoreCaseAndIdNot(Long businessId, String name, Long id);
    long countByBusinessIdAndActiveTrue(Long businessId);
}
