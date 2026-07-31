package com.smartprocure.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ProcurementResponseDto;
import com.smartprocure.services.ApprovalHistoryService;

import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class ApprovalHistoryServiceImpl implements ApprovalHistoryService {

	@Override
	public List<ProcurementResponseDto> getPendingProcurementCases(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponseDto submitProcurementCase(Long csId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponseDto approveProcurementCase(Long csId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApiResponseDto rejectProcurementCase(Long csId) {
		// TODO Auto-generated method stub
		return null;
	}

}
