package com.smartprocure.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ApprovalMatrixRequestDto {

    @NotNull(message = "Minimum amount is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum amount cannot be negative")
    private BigDecimal minAmount;

    @NotNull(message = "Maximum amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum amount must be greater than zero")
    private BigDecimal maxAmount;

    @NotNull(message = "Approval level is required")
    @Min(value = 1, message = "Approval level must be at least 1")
    private Integer approvalLevel;

    @NotNull(message = "Approver ID is required")
    private Long approverId;

    @NotNull(message = "Company ID is required")
    private Long companyId;
}