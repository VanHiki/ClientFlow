package com.clientflow.backend.domain.service;


import com.clientflow.backend.common.entity.BaseEntity;
import com.clientflow.backend.domain.business.Business;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "services",
        indexes = {
                @Index(name = "idx_services_business_id", columnList = "business_id"),
                @Index(name = "idx_services_business_active", columnList = "business_id, active")
        }
)
public class ServiceOffering extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
