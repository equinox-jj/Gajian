package com.myproject.gajian.salarycomponent.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "salary_component")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryComponent {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SalaryComponentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "calc_method", nullable = false, length = 30)
    private CalcMethod calcMethod;

    @Column(name = "is_taxable", nullable = false)
    private boolean taxable;
}
