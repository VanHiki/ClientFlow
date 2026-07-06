package com.clientflow.backend.domain.workinghour;

import com.clientflow.backend.common.entity.BaseEntity;
import com.clientflow.backend.domain.staff.StaffProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "working_hours",
        indexes = {
                @Index(name = "idx_working_hours_staff_profile_id", columnList = "staff_profile_id"),
                @Index(name = "idx_working_hours_staff_day", columnList = "staff_profile_id, day_of_week")
        }
)
public class WorkingHour extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "staff_profile_id", nullable = false)
    private StaffProfile staffProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 20)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;
}
