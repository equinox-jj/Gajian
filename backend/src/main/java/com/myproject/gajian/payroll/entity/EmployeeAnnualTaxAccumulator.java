package com.myproject.gajian.payroll.entity;

import com.myproject.gajian.employee.entity.Employee;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "employee_annual_tax_accumulator",
        uniqueConstraints = @UniqueConstraint(columnNames = {"employee_id", "tax_year"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeAnnualTaxAccumulator {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "tax_year", nullable = false)
    private Integer taxYear;

    @Column(name = "accumulated_gross_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal accumulatedGrossIncome;

    @Column(name = "accumulated_pph21_withheld", nullable = false, precision = 15, scale = 2)
    private BigDecimal accumulatedPph21Withheld;
}
