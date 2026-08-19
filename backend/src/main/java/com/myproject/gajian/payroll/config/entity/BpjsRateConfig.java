package com.myproject.gajian.payroll.config.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "bpjs_rate_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BpjsRateConfig {

    @Id
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BpjsProgram program;

    @Column(name = "employee_percentage", nullable = false, precision = 7, scale = 4)
    private BigDecimal employeePercentage;

    @Column(name = "employer_percentage", nullable = false, precision = 7, scale = 4)
    private BigDecimal employerPercentage;

    @Column(name = "salary_cap_amount", precision = 15, scale = 2)
    private BigDecimal salaryCapAmount;

    @Column(name = "effective_year", nullable = false)
    private Integer effectiveYear;
}
