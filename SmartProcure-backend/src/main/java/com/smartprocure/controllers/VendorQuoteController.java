package com.smartprocure.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.VendorQuoteRequestDto;
import com.smartprocure.dtos.VendorQuoteResponseDto;
import com.smartprocure.services.VendorQuoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vendor-quotes")
public class VendorQuoteController {

	private final VendorQuoteService vendorQuoteService;
	
	@GetMapping("/{quoteId}")
	public ResponseEntity<VendorQuoteResponseDto> getVendorQuote(@PathVariable Long quoteId)
	{
		return ResponseEntity.ok(vendorQuoteService.getVendorQuote(quoteId));
	}
	
	
	//TODO ranked
	@GetMapping("/procurement-case/{csId}")
	public ResponseEntity<List<VendorQuoteResponseDto>> getRankedVendorQuotes(@PathVariable Long csId)
	{	
		return ResponseEntity.ok(vendorQuoteService.getRankedVendorQuotes(csId));
	}
	
	@PostMapping
	public ResponseEntity<VendorQuoteResponseDto> addVendorQuote(@RequestBody @Valid VendorQuoteRequestDto vendorQuoteRequestDto)
	{
		return ResponseEntity.status(HttpStatus.CREATED).body(vendorQuoteService.addVendorQuote(vendorQuoteRequestDto));
	}
	
	
	@PutMapping("/{quoteId}")
	public ResponseEntity<VendorQuoteResponseDto> updateVendorQuote(
	        @PathVariable Long quoteId,
	        @RequestBody @Valid VendorQuoteRequestDto vendorQuoteRequestDto)
	{
		return ResponseEntity.ok(vendorQuoteService.updateVendorQuote(quoteId, vendorQuoteRequestDto));
	}
	
	
	@DeleteMapping("/{quoteId}")
	public ResponseEntity<ApiResponseDto> deleteVendorQuote(
	        @PathVariable Long quoteId)
	{
		return ResponseEntity.ok(vendorQuoteService.deleteVendorQuote(quoteId));
	}
	
	
	
	
	
}
