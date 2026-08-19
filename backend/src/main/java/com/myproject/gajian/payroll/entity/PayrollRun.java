package com.myproject.gajian.payroll.entity;

import com.myproject.gajian.security.entity.AppUser;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "payroll_run")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollRun {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_period_id", nullable = false, unique = true)
    private PayrollPeriod payrollPeriod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayrollRunStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_by_user_id")
    private AppUser runByUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "finalized_by_user_id")
    private AppUser finalizedByUser;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "finalized_at")
    private Instant finalizedAt;
}
