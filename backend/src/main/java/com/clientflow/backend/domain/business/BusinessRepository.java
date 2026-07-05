package com.clientflow.backend.domain.business;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BusinessRepository extends JpaRepository<Business, Long> {
    Optional<Business> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Optional<Business> findByOwnerId(Long ownerId);
}
