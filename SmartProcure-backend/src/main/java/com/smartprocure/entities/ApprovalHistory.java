package com.smartprocure.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "approval_history")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"approver", "procurementCase"})
public class ApprovalHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approval_id")
    private Long approvalId;

    @Column(name = "approval_cycle", nullable = false)
    private Integer approvalCycle;

    @Column(name = "approval_level", nullable = false)
    private Integer approvalLevel;

    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(length = 1000)
    private String remarks;

    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    @ManyToOne
    @JoinColumn(name = "procurement_case_id", nullable = false)
    private ProcurementCase procurementCase;
}
	
	
