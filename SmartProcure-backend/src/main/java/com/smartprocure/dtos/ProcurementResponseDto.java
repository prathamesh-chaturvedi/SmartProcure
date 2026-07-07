package com.smartprocure.dtos;

import java.math.BigDecimal;

import com.smartprocure.entities.ProcurementStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProcurementResponseDto {

    private Long procurementCaseId;

    private String procurementCode;

    private Integer draftNumber;

    private String title;

    private String description;

    private String unit;

    private Integer quantity;

    private BigDecimal packageAmount;

    private String recommendedVendor;

    private ProcurementStatus status;

    private String csPdfPath;
}