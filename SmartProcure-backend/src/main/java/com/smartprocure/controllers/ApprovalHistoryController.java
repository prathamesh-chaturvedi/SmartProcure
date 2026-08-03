package com.smartprocure.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ApprovalDecisionDto;
import com.smartprocure.dtos.ProcurementResponseDto;
import com.smartprocure.services.ApprovalHistoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/approvals")
public class ApprovalHistoryController {
	
	private final ApprovalHistoryService approvalHistoryService;
	
	
	
	@GetMapping("/pending-approval/{userId}")
	public ResponseEntity<List<ProcurementResponseDto>> getPendingProcurementCases()
	{
		return ResponseEntity.ok(
				approvalHistoryService.getPendingProcurementCases());
	}
	
	
	//TODO also add create approval history method in serviceimpl
	@PatchMapping("/{csId}/submit")
	public ResponseEntity<ApiResponseDto> submitProcurementCase(@PathVariable Long csId)
	{
		return ResponseEntity.ok(approvalHistoryService.submitProcurementCase(csId));
	}
	
	
	//TODO before approve check whether approver has authority in ApprovalMatrix
	@PatchMapping("/{csId}/approve")
	public ResponseEntity<ApiResponseDto> approveProcurementCase(@PathVariable Long csId, ApprovalDecisionDto approvalDecisionDto)
	{
		return ResponseEntity.ok(approvalHistoryService.approveProcurementCase(csId, approvalDecisionDto));
	}
	
	
	//TODO before rejecting check whether approver has authority in ApprovalMatrix
	@PatchMapping("/{csId}/reject")
	public ResponseEntity<ApiResponseDto> rejectProcurementCase(@PathVariable Long csId, ApprovalDecisionDto approvalDecisionDto)
	{
		return ResponseEntity.ok(approvalHistoryService.rejectProcurementCase(csId, approvalDecisionDto));
	}
}
