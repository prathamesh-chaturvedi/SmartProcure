
package com.smartprocure.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VendorQuoteRequestDto {

    @NotBlank(message = "Vendor name is required")
    @Size(max = 100, message = "Vendor name cannot exceed 100 characters")
    private String vendorName;

    @NotNull(message = "Quoted rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quoted rate must be greater than 0")
    @Digits(integer = 12, fraction = 2, message = "Quoted rate can have up to 12 digits and 2 decimal places")
    private BigDecimal quotedRate;

    @NotNull(message = "Quoted amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Quoted amount must be greater than 0")
    @Digits(integer = 15, fraction = 2, message = "Quoted amount can have up to 15 digits and 2 decimal places")
    private BigDecimal quotedAmount;

    @Size(max = 255, message = "Payment terms cannot exceed 255 characters")
    private String paymentTerms;

    @Size(max = 255, message = "Transportation terms cannot exceed 255 characters")
    private String transportationTerms;

    @Size(max = 255, message = "Delivery period cannot exceed 255 characters")
    private String deliveryPeriod;

    @Size(max = 100, message = "Validity cannot exceed 100 characters")
    private String validity;

    @Size(max = 100, message = "Warranty cannot exceed 100 characters")
    private String warranty;

    @Size(max = 2000, message = "Terms and conditions cannot exceed 2000 characters")
    private String termsAndConditions;

    @Size(max = 1000, message = "Remarks cannot exceed 1000 characters")
    private String remarks;

    @NotNull(message = "Quote date is required")
    @PastOrPresent(message = "Quote date cannot be in the future")
    private LocalDate quoteDate;

    @Size(max = 500, message = "Quote PDF path cannot exceed 500 characters")
    private String quotePdfPath;

    @NotNull(message = "Procurement Case ID is required")
    private Long procurementCaseId;
}