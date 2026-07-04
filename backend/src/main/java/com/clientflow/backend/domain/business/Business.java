package com.clientflow.backend.domain.business;


import com.clientflow.backend.common.BookingConstants;
import com.clientflow.backend.common.entity.BaseEntity;
import com.clientflow.backend.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "businesses", indexes = {
        @Index(name = "idx_businesses_owner_id", columnList = "owner_id")
    }
)
public class Business extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(nullable = false, unique = true, length = 120)
    private String slug;

    @Column(length = 30)
    private String phone;

    @Column(length = 160)
    private String email;

    @Column(length = 255)
    private String address;

    @Builder.Default
    @Column(nullable = false, length = 80)
    private String timezone = BookingConstants.DEFAULT_TIMEZONE;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
