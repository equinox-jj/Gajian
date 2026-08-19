package com.myproject.gajian.payroll.entity;

import com.myproject.gajian.overtime.entity.OvertimeRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

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
