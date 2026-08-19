package com.myproject.gajian.payroll.config.entity;

import com.myproject.gajian.employee.entity.TerCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ter_rate_bracket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerRateBracket {

    @Id
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private TerCategory category;

    @Column(name = "income_from", nullable = false, precision = 15, scale = 2)
    private BigDecimal incomeFrom;

    @Column(name = "income_to", precision = 15, scale = 2)
    private BigDecimal incomeTo;

    @Column(name = "rate_percentage", nullable = false, precision = 7, scale = 4)
    private BigDecimal ratePercentage;

    @Column(name = "effective_year", nullable = false)
    private Integer effectiveYear;
}
