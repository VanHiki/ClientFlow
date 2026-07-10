package com.clientflow.backend.domain.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByBusinessIdAndRecipientId(Long businessId, Long recipientId, Pageable pageable);

    Page<Notification> findByRecipientId(Long recipientId, Pageable pageable);

    Optional<Notification> findByIdAndRecipientId(Long id, Long recipientId);

    Optional<Notification> findByIdAndBusinessIdAndRecipientId(Long id, Long businessId, Long recipientId);

    long countByBusinessIdAndRecipientIdAndReadAtIsNull(Long businessId, Long recipientId);

    long countByRecipientIdAndReadAtIsNull(Long recipientId);

    List<Notification> findByBusinessIdAndRecipientIdAndReadAtIsNull(Long businessId, Long recipientId);

    List<Notification> findByRecipientIdAndReadAtIsNull(Long recipientId);
}
