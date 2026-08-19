package com.myproject.gajian.payroll.config.entity;

import com.myproject.gajian.overtime.entity.OvertimeDayCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
