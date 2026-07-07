package com.smartprocure.dtos;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApprovalMatrixResponseDto {

    private Long matrixId;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private Integer approvalLevel;

    private Long approverId;

    private String approverName;

    private Long companyId;

    private String companyName;
}