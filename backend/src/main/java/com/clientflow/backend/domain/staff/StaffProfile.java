package com.clientflow.backend.domain.staff;

import com.clientflow.backend.common.entity.BaseEntity;
import com.clientflow.backend.domain.business.Business;
import com.clientflow.backend.domain.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "staff_profiles",
        indexes = {
                @Index(name = "idx_staff_profiles_business_id", columnList = "business_id"),
                @Index(name = "idx_staff_profiles_business_active", columnList = "business_id, active"),
                @Index(name = "idx_staff_profiles_user_id", columnList = "user_id")
        }
)
public class StaffProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(length = 160)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(length = 80)
    private String position;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
