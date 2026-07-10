package com.clientflow.backend.domain.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {

    Page<ServiceOffering> findByBusinessId(Long businessId, Pageable pageable);

    @Query("""
            select service
            from ServiceOffering service
            where service.business.id = :businessId
              and (:active is null or service.active = :active)
              and (
                    :keyword is null
                    or lower(service.name) like lower(concat('%', :keyword, '%'))
                    or lower(coalesce(service.description, '')) like lower(concat('%', :keyword, '%'))
              )
            """)
    Page<ServiceOffering> search(
            @Param("businessId") Long businessId,
            @Param("keyword") String keyword,
            @Param("active") Boolean active,
            Pageable pageable
    );
    Optional<ServiceOffering> findByIdAndBusinessId(Long id, Long businessId);
    List<ServiceOffering> findByBusinessIdAndActiveTrueOrderByCreatedAtDesc(Long businessId);
    boolean existsByBusinessIdAndNameIgnoreCase(Long businessId, String name);
    boolean existsByBusinessIdAndNameIgnoreCaseAndIdNot(Long businessId, String name, Long id);
    long countByBusinessIdAndActiveTrue(Long businessId);
}
