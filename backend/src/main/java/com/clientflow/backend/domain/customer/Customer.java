package com.clientflow.backend.domain.customer;

import com.clientflow.backend.common.entity.BaseEntity;
import com.clientflow.backend.domain.business.Business;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_customers_business_phone", columnNames = {"business_id", "phone"}),
                @UniqueConstraint(name = "uk_customers_business_email", columnNames = {"business_id", "email"})
        },
        indexes = {
                @Index(name = "idx_customers_business_id", columnList = "business_id"),
                @Index(name = "idx_customers_business_active", columnList = "business_id, active")
        }
)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, length = 30)
    private String phone;

    @Column(length = 160)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}