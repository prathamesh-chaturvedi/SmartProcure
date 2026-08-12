package com.smartprocure.services;

import org.springframework.core.io.Resource;

public interface PdfService {

	String generateComparativeStatementPdf(Long prourementCaseId);
	
	Resource downloadComparativeStatementPdf(Long procurementCaseId);
}
