package com.smartprocure.services.impl;

import org.springframework.stereotype.Service;

import com.smartprocure.services.PdfService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

	@Override
	public String generateComparativeStatementPdf(Long prourementCaseId) {
		
		// TODO:
        // 1. Fetch Procurement Case
        // 2. Fetch Vendor Quotes
        // 3. Prepare HTML
        // 4. Convert HTML to PDF
        // 5. Save PDF
        // 6. Return file path
		
		return null;
	}

}
