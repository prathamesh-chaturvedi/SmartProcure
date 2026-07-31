package com.smartprocure.entities;

import java.math.BigDecimal;

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
@Table(name = "procurement_cases")
@Setter
@Getter
@ToString(exclude = {"createdBy"})
@NoArgsConstructor
public class ProcurementCase extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "procurement_case_id")
	private Long procurementCaseId;
	
	
	@Column(name = "procurement_code", unique = true, nullable = false)
	private String procurementCode;
	
	@Column(name = "draft_number")
	private Integer draftNumber = 1;
	
	@Column(length = 50, nullable = false)
	private String title;
	
	@Column(length = 250, nullable = false)
	private String description;
	
	@Column(length = 50, nullable = false)
	private String unit;
	
	@Column(nullable = false)
	private Integer quantity;
	
	@Column(name = "package_amount")
	private BigDecimal packageAmount;
	
	@Column(name = "recommended_vendor")
	private String recommendedVendor;
	
	@Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProcurementStatus status = ProcurementStatus.DRAFT;
	
	@Column(name = "cs_pdf_path")
	private String csPdfPath;
	
	@ManyToOne
	@JoinColumn(name = "created_by_id", nullable = false)
	private User createdBy;
	

}
