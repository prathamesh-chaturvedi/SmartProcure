package com.smartprocure.services;


import java.util.List;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ProcurementResponseDto;

public interface ApprovalHistoryService {

	List<ProcurementResponseDto> getPendingProcurementCases(Long userId);

	ApiResponseDto submitProcurementCase(Long csId);

	ApiResponseDto approveProcurementCase(Long csId);

	ApiResponseDto rejectProcurementCase(Long csId);

}
