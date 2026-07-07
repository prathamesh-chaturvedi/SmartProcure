package com.smartprocure.controllers;


import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ProcurementRequestDto;
import com.smartprocure.dtos.ProcurementResponseDto;
import com.smartprocure.entities.ProcurementStatus;
import com.smartprocure.services.ProcurementCaseService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/procurement-cases")
// TODO later remove unnecessary mapping after jwt added
public class ProcurementCaseController {
	
	private final ProcurementCaseService procurementCaseService;
	
	
	//FIXME later when jwt added check whether cs belongs to the valid user/userId
	@GetMapping("/{csId}")
	public ResponseEntity<ProcurementResponseDto> getProcurementCaseById(@PathVariable Long csId)
	{
		return ResponseEntity.ok(
				procurementCaseService.getProcurementCaseById(csId));
	}
	
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<Page<ProcurementResponseDto>> getProcurementCases(
	        @PathVariable Long userId,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) ProcurementStatus status,
	        @RequestParam(required = false) String procurementCode,
	        @RequestParam(required = false) String title,
	        @RequestParam(required = false) BigDecimal minAmount,
	        @RequestParam(required = false) BigDecimal maxAmount,
	        @RequestParam(required = false) LocalDate fromDate,
	        @RequestParam(required = false) LocalDate toDate) {

	    return ResponseEntity.ok(
	            procurementCaseService.getProcurementCases(
	            			userId,
	            	        page,
	            	        size,
	            	        status,
	            	        procurementCode,
	            	        title,
	            	        minAmount,
	            	        maxAmount,
	            	        fromDate,
	            	        toDate));
	}
	
	
	
	//FIXME remove userId once jwt added, also Modelmap User entity seperately
	@PostMapping("/user/{userId}")
	public ResponseEntity<ProcurementResponseDto> createProcurementCase(
	        @PathVariable Long userId,
	        @RequestBody @Valid ProcurementRequestDto procurementRequestDto)
	{
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(procurementCaseService
						.createProcurementCase(userId, procurementRequestDto));
	}
	
	
	// TODO Allow update only when status == DRAFT
	@PutMapping("/{csId}")
	public ResponseEntity<ProcurementResponseDto> updateProcurementCase(
			@PathVariable Long csId, 
			@RequestBody @Valid ProcurementRequestDto procurementRequestDto)
	{
		return ResponseEntity.ok(procurementCaseService.updateProcurementCase(csId, procurementRequestDto));
	}
	
	
	// TODO Allow delete only when status == DRAFT
	@DeleteMapping("/{csId}")
	public ResponseEntity<ApiResponseDto> deleteProcurementCase(@PathVariable Long csId)
	{
		return ResponseEntity.ok(procurementCaseService.deleteProcurementCase(csId));
	}
	
	
	
	
}
