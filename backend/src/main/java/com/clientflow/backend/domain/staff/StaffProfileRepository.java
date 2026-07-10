package com.clientflow.backend.domain.staff;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StaffProfileRepository extends JpaRepository<StaffProfile, Long> {

    Page<StaffProfile> findByBusinessId(Long businessId, Pageable pageable);

    @Query("""
            select staff
            from StaffProfile staff
            where staff.business.id = :businessId
              and (:active is null or staff.active = :active)
              and (
                    :keyword is null
                    or lower(staff.fullName) like lower(concat('%', :keyword, '%'))
                    or lower(coalesce(staff.email, '')) like lower(concat('%', :keyword, '%'))
                    or staff.phone like concat('%', :keyword, '%')
                    or lower(coalesce(staff.position, '')) like lower(concat('%', :keyword, '%'))
              )
            """)
    Page<StaffProfile> search(
            @Param("businessId") Long businessId,
            @Param("keyword") String keyword,
            @Param("active") Boolean active,
            Pageable pageable
    );

    Optional<StaffProfile> findByIdAndBusinessId(Long id, Long businessId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select staff
            from StaffProfile staff
            where staff.id = :staffId
              and staff.business.id = :businessId
            """)
    Optional<StaffProfile> findByIdAndBusinessIdForUpdate(
            @Param("staffId") Long staffId,
            @Param("businessId") Long businessId
    );

    Optional<StaffProfile> findByUserId(Long userId);

    boolean existsByBusinessIdAndEmailIgnoreCase(Long businessId, String email);

    boolean existsByBusinessIdAndEmailIgnoreCaseAndIdNot(Long businessId, String email, Long id);

    long countByBusinessIdAndActiveTrue(Long businessId);
}
