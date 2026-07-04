package com.clientflow.backend.domain.business;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BusinessRepository extends CrudRepository<Business, Long> {
    Optional<Business> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Optional<Business> findByOwnerId(Long ownerId);
}
