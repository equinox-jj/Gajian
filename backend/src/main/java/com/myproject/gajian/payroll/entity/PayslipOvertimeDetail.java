package com.myproject.gajian.payroll.entity;

import com.myproject.gajian.overtime.entity.OvertimeRequest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payslip_overtime_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayslipOvertimeDetail {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payslip_id", nullable = false)
    private Payslip payslip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "overtime_request_id", nullable = false)
    private OvertimeRequest overtimeRequest;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal hours;

    @Column(name = "multiplier_used", nullable = false, precision = 5, scale = 2)
    private BigDecimal multiplierUsed;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
}
