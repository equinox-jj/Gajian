package com.myproject.gajian.reimbursement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reimbursement_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReimbursementCategory {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "max_amount_per_claim", precision = 15, scale = 2)
    private BigDecimal maxAmountPerClaim;

    @Column(name = "requires_receipt", nullable = false)
    private boolean requiresReceipt;
}
