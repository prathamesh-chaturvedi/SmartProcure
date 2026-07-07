package com.smartprocure.controllers;

import java.util.List;

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
import com.smartprocure.dtos.ApprovalMatrixRequestDto;
import com.smartprocure.dtos.ApprovalMatrixResponseDto;
import com.smartprocure.services.ApprovalMatrixService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/approval-matrices")
public class ApprovalMatrixController {

	private final ApprovalMatrixService approvalMatrixService;
	
	
	@GetMapping("/{matrixid}")
	public ResponseEntity<ApprovalMatrixResponseDto> getApprovalMatrix(@PathVariable Long matrixId)
	{
		return ResponseEntity.ok(approvalMatrixService.getApprovalMatrix(matrixId));
	}
	
	
	@GetMapping("/company/{companyId}")
	public ResponseEntity<List<ApprovalMatrixResponseDto>> getApprovalMatrices(@PathVariable Long companyId)
	{
		return ResponseEntity.ok(approvalMatrixService.getApprovalMatrices(companyId));
	}

	
	@PostMapping
	public ResponseEntity<ApprovalMatrixResponseDto> addApprovalMatrix(@RequestBody @Valid ApprovalMatrixRequestDto approvalMatrixRequestDto)
	{
		return ResponseEntity.ok(approvalMatrixService.addApprovalMatrix(approvalMatrixRequestDto));
	}

	
	@PutMapping("/{matrixId}")
	public ResponseEntity<ApprovalMatrixResponseDto> updateApprovalMatrix(
			@PathVariable Long matrixId, @RequestBody @Valid ApprovalMatrixRequestDto approvalMatrixRequestDto)
	{
		return ResponseEntity.ok(approvalMatrixService.updateApprovalMatrix(matrixId, approvalMatrixRequestDto));
	}
	
	
	@DeleteMapping("/{matrixId}")
	public ResponseEntity<ApiResponseDto> deleteApprovalMatrix(@PathVariable Long matrixId)
	{
		return ResponseEntity.ok(approvalMatrixService.deleteApprovalMatrix(matrixId));
	}
	

}
