package com.myproject.gajian.payroll.config.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "pph21_annual_bracket")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pph21AnnualBracket {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "bracket_from", nullable = false, precision = 15, scale = 2)
    private BigDecimal bracketFrom;

    @Column(name = "bracket_to", precision = 15, scale = 2)
    private BigDecimal bracketTo;

    @Column(name = "rate_percentage", nullable = false, precision = 7, scale = 4)
    private BigDecimal ratePercentage;

    @Column(name = "effective_year", nullable = false)
    private Integer effectiveYear;
}
