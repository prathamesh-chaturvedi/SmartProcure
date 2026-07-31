package com.smartprocure.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class ProcurementCaseController {

    private final ProcurementCaseService procurementCaseService;


    // No change
    @GetMapping("/{csId}")
    public ResponseEntity<ProcurementResponseDto> getProcurementCaseById(
            @PathVariable Long csId) {

        return ResponseEntity.ok(
                procurementCaseService.getProcurementCaseById(csId));
    }


    /*
     * CHANGED:
     * Removed userId from URL.
     * Service will obtain current userId and companyId from CurrentUserService.
     */
    @GetMapping
    public ResponseEntity<Page<ProcurementResponseDto>> getProcurementCases(

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


    /*
     * CHANGED:
     * Removed userId from URL.
     * Service will automatically use logged-in user's information.
     */
    @PostMapping
    public ResponseEntity<ProcurementResponseDto> createProcurementCase(
            @RequestBody @Valid ProcurementRequestDto procurementRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(procurementCaseService.createProcurementCase(procurementRequestDto));
    }


    /*
     * TODO:
     * Allow update only when Procurement Status == DRAFT.
     */
    @PutMapping("/{csId}")
    public ResponseEntity<ProcurementResponseDto> updateProcurementCase(
            @PathVariable Long csId,
            @RequestBody @Valid ProcurementRequestDto procurementRequestDto) {

        return ResponseEntity.ok(
                procurementCaseService.updateProcurementCase(
                        csId,
                        procurementRequestDto));
    }


    /*
     * TODO:
     * Allow delete only when Procurement Status == DRAFT.
     */
    @DeleteMapping("/{csId}")
    public ResponseEntity<ApiResponseDto> deleteProcurementCase(
            @PathVariable Long csId) {

        return ResponseEntity.ok(
                procurementCaseService.deleteProcurementCase(csId));
    }

}