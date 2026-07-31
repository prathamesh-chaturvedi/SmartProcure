package com.smartprocure.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartprocure.entities.VendorQuote;

public interface VendorQuoteRepository extends JpaRepository<VendorQuote, Long>{
	
	List<VendorQuote> findByProcurementCaseProcurementCaseIdOrderByEffectiveCostAsc(Long csId);
}
