package com.myproject.gajian.leave.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "leave_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveType {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "default_days_per_year", nullable = false)
    private Integer defaultDaysPerYear;

    @Column(name = "is_paid", nullable = false)
    private boolean paid;

    @Column(name = "requires_attachment", nullable = false)
    private boolean requiresAttachment;
}
