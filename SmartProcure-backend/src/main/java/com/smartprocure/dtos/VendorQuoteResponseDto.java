package com.smartprocure.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VendorQuoteResponseDto {

    private Long quoteId;

    private String vendorName;

    private BigDecimal quotedRate;

    private BigDecimal quotedAmount;

    private BigDecimal transportationCost;

    private BigDecimal effectiveCost;

    private String paymentTerms;

    private String deliveryPeriod;

    private String validity;

    private String warranty;

    private String remarks;

    private LocalDate quoteDate;

    private String quotePdfPath;

    private Long procurementCaseId;

    private String procurementCode;
}