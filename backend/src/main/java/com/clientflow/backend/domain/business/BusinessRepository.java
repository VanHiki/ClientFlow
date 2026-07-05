package com.clientflow.backend.domain.business;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Business> findByOwnerId(Long ownerId, Pageable pageable);

    Optional<Business> findByIdAndOwnerId(Long id, Long ownerId);

    boolean existsByOwnerIdAndSlug(Long ownerId, String slug);
}
