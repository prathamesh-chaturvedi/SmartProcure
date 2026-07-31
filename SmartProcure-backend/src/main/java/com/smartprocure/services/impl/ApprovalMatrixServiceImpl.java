package com.smartprocure.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ApprovalMatrixRequestDto;
import com.smartprocure.dtos.ApprovalMatrixResponseDto;
import com.smartprocure.services.ApprovalMatrixService;

import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class ApprovalMatrixServiceImpl implements ApprovalMatrixService {

	@Override
	public List<ApprovalMatrixResponseDto> getApprovalMatrices(Long companyId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApprovalMatrixResponseDto getApprovalMatrix(Long matrixId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApprovalMatrixResponseDto addApprovalMatrix(ApprovalMatrixRequestDto approvalMatrixRequestDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApprovalMatrixResponseDto updateApprovalMatrix(Long matrixId,
			ApprovalMatrixRequestDto approvalMatrixRequestDto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponseDto deleteApprovalMatrix(Long matrixId) {
		// TODO Auto-generated method stub
		return null;
	}

}
