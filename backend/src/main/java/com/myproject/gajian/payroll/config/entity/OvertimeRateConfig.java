package com.myproject.gajian.payroll.config.entity;

import com.myproject.gajian.overtime.entity.OvertimeDayCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "overtime_rate_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OvertimeRateConfig {

    @Id
    @UuidGenerator
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_category", nullable = false, length = 20)
    private OvertimeDayCategory dayCategory;

    @Column(name = "hour_from", nullable = false)
    private Integer hourFrom;

    @Column(name = "hour_to")
    private Integer hourTo;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal multiplier;

    @Column(name = "effective_year", nullable = false)
    private Integer effectiveYear;
}
