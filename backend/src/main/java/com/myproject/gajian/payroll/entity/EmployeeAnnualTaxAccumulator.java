package com.myproject.gajian.payroll.entity;

import com.myproject.gajian.employee.entity.Employee;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

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
