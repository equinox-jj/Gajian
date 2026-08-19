package com.myproject.gajian.payroll.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(
        name = "payroll_period",
        uniqueConstraints = @UniqueConstraint(columnNames = {"period_month", "period_year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollPeriod {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "period_month", nullable = false)
    private Integer periodMonth;

    @Column(name = "period_year", nullable = false)
    private Integer periodYear;

    @Column(name = "is_final_tax_period", nullable = false)
    private boolean finalTaxPeriod;
}
