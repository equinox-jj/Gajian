package com.myproject.gajian.employee.entity;

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
@Table(name = "ptkp_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PtkpStatus {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "ter_category", nullable = false, length = 1)
    private TerCategory terCategory;

    @Column(name = "annual_ptkp_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal annualPtkpAmount;

    @Column(name = "effective_year", nullable = false)
    private Integer effectiveYear;
}
