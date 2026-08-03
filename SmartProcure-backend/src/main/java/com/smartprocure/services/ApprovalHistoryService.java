package com.smartprocure.services;


import java.util.List;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ApprovalDecisionDto;
import com.smartprocure.dtos.ProcurementResponseDto;

public interface ApprovalHistoryService {

	List<ProcurementResponseDto> getPendingProcurementCases();

	ApiResponseDto submitProcurementCase(Long csId);

	ApiResponseDto approveProcurementCase(Long csId, ApprovalDecisionDto approvalDecisionDto);

	ApiResponseDto rejectProcurementCase(Long csId, ApprovalDecisionDto approvalDecisionDto);

}
