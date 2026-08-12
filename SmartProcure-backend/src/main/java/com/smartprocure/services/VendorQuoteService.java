package com.smartprocure.services;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.VendorQuoteRequestDto;
import com.smartprocure.dtos.VendorQuoteResponseDto;

public interface VendorQuoteService {

	VendorQuoteResponseDto getVendorQuote(Long quoteId, Long csId);

	List<VendorQuoteResponseDto> getRankedVendorQuotes(Long csId);

	VendorQuoteResponseDto addVendorQuote(VendorQuoteRequestDto vendorQuoteRequestDto);

	VendorQuoteResponseDto updateVendorQuote(Long quoteId, VendorQuoteRequestDto vendorQuoteRequestDto);

	ApiResponseDto deleteVendorQuote(Long quoteId);

	void uploadQuotePdf(Long quoteId, MultipartFile file);
	
	Resource getQuotePdf(Long quoteId);
}
