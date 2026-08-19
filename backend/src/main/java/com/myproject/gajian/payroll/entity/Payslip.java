package com.myproject.gajian.payroll.entity;

import com.myproject.gajian.employee.entity.Employee;
import com.myproject.gajian.employee.entity.TerCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Table(name = "payslip")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payslip {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_run_id", nullable = false)
    private PayrollRun payrollRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "base_salary", nullable = false, precision = 15, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "total_allowance", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAllowance;

    @Column(name = "total_overtime_pay", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalOvertimePay;

    @Column(name = "total_deduction_other", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalDeductionOther;

    @Column(name = "gross_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal grossIncome;

    @Column(name = "is_final_tax_calculation", nullable = false)
    private boolean finalTaxCalculation;

    @Enumerated(EnumType.STRING)
    @Column(name = "ter_category_used", length = 1)
    private TerCategory terCategoryUsed;

    @Column(name = "pph21_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal pph21Amount;

    @Column(name = "bpjs_kesehatan_employee", nullable = false, precision = 15, scale = 2)
    private BigDecimal bpjsKesehatanEmployee;

    @Column(name = "bpjs_kesehatan_employer", nullable = false, precision = 15, scale = 2)
    private BigDecimal bpjsKesehatanEmployer;

    @Column(name = "bpjs_jht_employee", nullable = false, precision = 15, scale = 2)
    private BigDecimal bpjsJhtEmployee;

    @Column(name = "bpjs_jht_employer", nullable = false, precision = 15, scale = 2)
    private BigDecimal bpjsJhtEmployer;

    @Column(name = "bpjs_jkk_employer", nullable = false, precision = 15, scale = 2)
    private BigDecimal bpjsJkkEmployer;

    @Column(name = "bpjs_jkm_employer", nullable = false, precision = 15, scale = 2)
    private BigDecimal bpjsJkmEmployer;

    @Column(name = "bpjs_jp_employee", nullable = false, precision = 15, scale = 2)
    private BigDecimal bpjsJpEmployee;

    @Column(name = "bpjs_jp_employer", nullable = false, precision = 15, scale = 2)
    private BigDecimal bpjsJpEmployer;

    @Column(name = "net_pay", nullable = false, precision = 15, scale = 2)
    private BigDecimal netPay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PayslipStatus status;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
