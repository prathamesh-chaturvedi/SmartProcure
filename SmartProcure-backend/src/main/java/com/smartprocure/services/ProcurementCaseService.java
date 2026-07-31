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

    /*
     * CHANGED:
     * Removed userId.
     * Logged-in user's details will be obtained from CurrentUserService.
     */
    Page<ProcurementResponseDto> getProcurementCases(
            int page,
            int size,
            ProcurementStatus status,
            String procurementCode,
            String title,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            LocalDate fromDate,
            LocalDate toDate);

    /*
     * CHANGED:
     * Removed userId.
     * Logged-in user will become the creator automatically.
     */
    ProcurementResponseDto createProcurementCase(
            ProcurementRequestDto procurementRequestDto);

    ProcurementResponseDto updateProcurementCase(
            Long csId,
            ProcurementRequestDto procurementRequestDto);

    ApiResponseDto deleteProcurementCase(Long csId);

}
