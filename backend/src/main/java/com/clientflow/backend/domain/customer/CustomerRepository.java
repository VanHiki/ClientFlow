package com.clientflow.backend.domain.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByBusinessId(Long businessId, Pageable pageable);

    Optional<Customer> findByIdAndBusinessId(Long id, Long businessId);

    boolean existsByBusinessIdAndPhone(Long businessId, String phone);

    boolean existsByBusinessIdAndEmailIgnoreCase(Long businessId, String email);
}