package com.myproject.gajian.employee.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

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
