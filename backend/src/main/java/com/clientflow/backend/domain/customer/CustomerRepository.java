package com.clientflow.backend.domain.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByBusinessId(Long businessId, Pageable pageable);

    @Query("""
            select customer
            from Customer customer
            where customer.business.id = :businessId
              and (:active is null or customer.active = :active)
              and (
                    :keyword is null
                    or lower(customer.fullName) like lower(concat('%', :keyword, '%'))
                    or customer.phone like concat('%', :keyword, '%')
                    or lower(coalesce(customer.email, '')) like lower(concat('%', :keyword, '%'))
              )
            """)
    Page<Customer> search(
            @Param("businessId") Long businessId,
            @Param("keyword") String keyword,
            @Param("active") Boolean active,
            Pageable pageable
    );

    Optional<Customer> findByIdAndBusinessId(Long id, Long businessId);

    boolean existsByBusinessIdAndPhone(Long businessId, String phone);

    boolean existsByBusinessIdAndPhoneAndIdNot(Long businessId, String phone, Long id);

    boolean existsByBusinessIdAndEmailIgnoreCase(Long businessId, String email);

    boolean existsByBusinessIdAndEmailIgnoreCaseAndIdNot(Long businessId, String email, Long id);

    Optional<Customer> findByBusinessIdAndPhone(Long businessId, String phone);

    long countByBusinessIdAndActiveTrue(Long businessId);
}
