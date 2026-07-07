package com.smartprocure.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "approval_matrix")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"approver", "company"})
public class ApprovalMatrix extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "matrix_id")
    private Long matrixId;

    @Column(name = "min_amount", nullable = false)
    private BigDecimal minAmount;

    @Column(name = "max_amount", nullable = false)
    private BigDecimal maxAmount;

    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel;

    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}