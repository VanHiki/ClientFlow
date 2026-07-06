package com.clientflow.backend.domain.staffservice;

import com.clientflow.backend.common.entity.BaseEntity;
import com.clientflow.backend.domain.service.ServiceOffering;
import com.clientflow.backend.domain.staff.StaffProfile;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "staff_services",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_staff_services_staff_service",
                        columnNames = {"staff_profile_id", "service_id"}
                )
        },
        indexes = {
                @Index(name = "idx_staff_services_staff_profile_id", columnList = "staff_profile_id"),
                @Index(name = "idx_staff_services_service_id", columnList = "service_id")
        }
)
public class StaffServiceAssignment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_profile_id", nullable = false)
    private StaffProfile staffProfile;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceOffering serviceOffering;
}
