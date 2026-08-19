package com.myproject.gajian.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

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
