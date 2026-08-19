package com.myproject.gajian.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "work_schedule_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkScheduleConfig {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "clock_in_time", nullable = false)
    private LocalTime clockInTime;

    @Column(name = "clock_out_time", nullable = false)
    private LocalTime clockOutTime;

    @Column(name = "late_tolerance_min", nullable = false)
    private Integer lateToleranceMin;
}
