package com.clientflow.backend.domain.businessexception;

import com.clientflow.backend.common.entity.BaseEntity;
import com.clientflow.backend.common.enums.BusinessExceptionType;
import com.clientflow.backend.domain.business.Business;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
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
        name = "business_exceptions",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_business_exceptions_business_date", columnNames = {"business_id", "date"})
        },
        indexes = {
                @Index(name = "idx_business_exceptions_business_date", columnList = "business_id, date")
        }
)
public class BusinessExceptionDay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BusinessExceptionType type;

    @Column(length = 255)
    private String reason;
}
