package com.smartprocure.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartprocure.entities.Company;

public interface CompanyRepository extends JpaRepository<Company, Long>{
	
	@Query("""
			SELECT c FROM Company c
			WHERE c.isActive = true
			AND (:name IS NULL OR LOWER(c.companyName) LIKE LOWER(CONCAT('%', :name, '%')))
			AND (:address IS NULL OR LOWER(c.address) LIKE LOWER(CONCAT('%', :address, '%')))
			""")
	Page<Company> searchCompanies(
	        @Param("name") String name,
	        @Param("address") String address,
	        Pageable pageable);
}
