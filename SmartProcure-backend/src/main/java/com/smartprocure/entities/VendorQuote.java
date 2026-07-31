package com.smartprocure.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "vendor_quotes")
@Getter
@Setter
@ToString(exclude = {"procurementCase"})
@NoArgsConstructor
public class VendorQuote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quote_id")
    private Long quoteId;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "quoted_rate", nullable = false)
    private BigDecimal quotedRate;

    @Column(name = "quoted_amount", nullable = false)
    private BigDecimal quotedAmount;

    @Column(name = "transportation_cost", nullable = false)
    private BigDecimal transportationCost = BigDecimal.ZERO;

    @Column(name = "effective_cost", nullable = false)
    private BigDecimal effectiveCost = BigDecimal.ZERO;

    @Column(name = "payment_terms")
    private String paymentTerms;

    @Column(name = "delivery_period")
    private String deliveryPeriod;

    @Column(name = "validity")
    private String validity;

    @Column(name = "warranty")
    private String warranty;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "quote_date", nullable = false)
    private LocalDate quoteDate;

    @Column(name = "quote_pdf_path")
    private String quotePdfPath;

    @ManyToOne
    @JoinColumn(name = "procurement_case_id", nullable = false)
    private ProcurementCase procurementCase;
}