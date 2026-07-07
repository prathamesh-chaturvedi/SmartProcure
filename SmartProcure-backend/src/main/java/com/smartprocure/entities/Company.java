package com.smartprocure.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "companies")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Company extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "company_id", unique = true)
	private Long companyId;
	@Column(name = "company_name", length = 50, unique = true, nullable = false)
	private String companyName;
	@Column(name = "company_address", nullable = false)
	private String address;
	@Column(name = "company_email", unique = true, nullable = false)
	private String email;
	@Column(length = 14, unique = true, nullable = false)
	private String phone;
	@Column(name = "is_active", nullable = false)
    private boolean isActive = true;
}
