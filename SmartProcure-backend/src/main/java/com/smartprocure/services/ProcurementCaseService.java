package com.smartprocure.services;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ProcurementRequestDto;
import com.smartprocure.dtos.ProcurementResponseDto;
import com.smartprocure.entities.ProcurementStatus;

public interface ProcurementCaseService {

	ProcurementResponseDto getProcurementCaseById(Long csId);
	
	
//	TODO if (minAmount != null && maxAmount != null
//    && minAmount.compareTo(maxAmount) > 0) {
//throw new BadRequestException("Minimum amount cannot be greater than maximum amount.");
//}
	Page<ProcurementResponseDto> getProcurementCases(Long userId, int page, int size, ProcurementStatus status, String procurementCode, String title,
			BigDecimal minAmount, BigDecimal maxAmount, LocalDate fromDate, LocalDate toDate);


	//FIXME remove userid once jwt
	ProcurementResponseDto createProcurementCase(Long userId, ProcurementRequestDto procurementRequestDto);

	ProcurementResponseDto updateProcurementCase(Long csId, ProcurementRequestDto procurementRequestDto);

	ApiResponseDto deleteProcurementCase(Long csId);

	

	
	
}

