package com.smartprocure.services;

import java.util.List;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ApprovalMatrixRequestDto;
import com.smartprocure.dtos.ApprovalMatrixResponseDto;

public interface ApprovalMatrixService {

	List<ApprovalMatrixResponseDto> getApprovalMatrices(Long companyId);

	ApprovalMatrixResponseDto getApprovalMatrix(Long matrixId);

	ApprovalMatrixResponseDto addApprovalMatrix(ApprovalMatrixRequestDto approvalMatrixRequestDto);

	ApprovalMatrixResponseDto updateApprovalMatrix(Long matrixId, ApprovalMatrixRequestDto approvalMatrixRequestDto);

	ApiResponseDto deleteApprovalMatrix(Long matrixId);

}
