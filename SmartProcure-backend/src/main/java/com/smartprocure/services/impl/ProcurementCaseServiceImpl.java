package com.smartprocure.services.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartprocure.custom_exceptions.InvalidInputException;
import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ProcurementRequestDto;
import com.smartprocure.dtos.ProcurementResponseDto;
import com.smartprocure.entities.Designation;
import com.smartprocure.entities.ProcurementCase;
import com.smartprocure.entities.ProcurementStatus;
import com.smartprocure.entities.User;
import com.smartprocure.entities.UserRole;
import com.smartprocure.repositories.ProcurementCaseRepository;
import com.smartprocure.security.AccessValidator;
import com.smartprocure.security.CurrentUserService;
import com.smartprocure.services.ProcurementCaseService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProcurementCaseServiceImpl implements ProcurementCaseService {

	private final ProcurementCaseRepository procurementCaseRepo;
	private final CurrentUserService currentUserService;
	private final ModelMapper mapper;
	private final AccessValidator accessValidator;
	
	@Override
	public ProcurementResponseDto getProcurementCaseById(Long csId) {

	    User currentUser = currentUserService.getCurrentUser();

	    ProcurementCase procurementCase = procurementCaseRepo.findById(csId)
	            .orElseThrow(() -> new ResourceNotFoundException("Invalid CS Id."));

	    // Validate that the Procurement Case belongs to the current user's company
	    accessValidator.validateUserAccess(procurementCase.getCreatedBy());

	    // Procurement Executive can access only their own Procurement Cases
	    if (currentUser.getDesignation() == Designation.PROCUREMENT_EXECUTIVE
	            && !currentUser.getUserId()
	                    .equals(procurementCase.getCreatedBy().getUserId())) {

	        throw new AccessDeniedException(
	                "You are not authorized to access other users' procurement cases.");
	    }

	    return mapper.map(procurementCase, ProcurementResponseDto.class);
	}

	@Override
	public Page<ProcurementResponseDto> getProcurementCases(
	        int page,
	        int size,
	        ProcurementStatus status,
	        String procurementCode,
	        String title,
	        BigDecimal minAmount,
	        BigDecimal maxAmount,
	        LocalDate fromDate,
	        LocalDate toDate) {

	    if (minAmount != null && maxAmount != null
	            && minAmount.compareTo(maxAmount) > 0) {
	        throw new InvalidInputException(
	                "Minimum amount cannot be greater than maximum amount.");
	    }

	    User currentUser = currentUserService.getCurrentUser();

	    Long companyId = null;
	    Long userId = null;

	    // MASTER_ADMIN can view everything
	    if (currentUser.getUserRole() != UserRole.MASTER_ADMIN) {

	        companyId = currentUser.getCompany().getCompanyId();

	        // Procurement Executive can view only their own Procurement Cases
	        if (currentUser.getDesignation() == Designation.PROCUREMENT_EXECUTIVE) {
	            userId = currentUser.getUserId();
	        }
	    }

	    LocalDateTime from = null;
	    LocalDateTime to = null;

	    if (fromDate != null) {
	        from = fromDate.atStartOfDay();
	    }

	    if (toDate != null) {
	        to = toDate.atTime(LocalTime.MAX);
	    }

	    Pageable pageable = PageRequest.of(page, size);

	    Page<ProcurementCase> procurementCases =
	            procurementCaseRepo.searchProcurementCases(
	                    companyId,
	                    userId,
	                    status,
	                    procurementCode,
	                    title,
	                    minAmount,
	                    maxAmount,
	                    from,
	                    to,
	                    pageable);

	    return procurementCases.map(
	            procurementCase -> mapper.map(procurementCase, ProcurementResponseDto.class));
	}

	@Override
	public ProcurementResponseDto createProcurementCase(
	        ProcurementRequestDto procurementRequestDto) {

	    User currentUser = currentUserService.getCurrentUser();

	    ProcurementCase procurementCase = mapper.map(
	            procurementRequestDto,
	            ProcurementCase.class);

	    procurementCase.setCreatedBy(currentUser);

	    // TODO Generate proper procurement code
	    procurementCase.setProcurementCode("PC-" + System.currentTimeMillis());

	    procurementCase.setDraftNumber(1);

	    procurementCase.setStatus(ProcurementStatus.DRAFT);

	    procurementCase.setDeleted(false);

	    ProcurementCase savedProcurementCase =
	            procurementCaseRepo.save(procurementCase);

	    return mapper.map(savedProcurementCase,
	            ProcurementResponseDto.class);
	}

	

	@Override
	public ProcurementResponseDto updateProcurementCase(
	        Long csId,
	        ProcurementRequestDto procurementRequestDto) {

	    User currentUser = currentUserService.getCurrentUser();

	    ProcurementCase procurementCase = procurementCaseRepo.findById(csId)
	            .orElseThrow(() -> new ResourceNotFoundException("Invalid CS Id."));

	    accessValidator.validateUserAccess(procurementCase.getCreatedBy());

	    if (currentUser.getDesignation() == Designation.PROCUREMENT_EXECUTIVE
	            && !currentUser.getUserId().equals(procurementCase.getCreatedBy().getUserId())) {

	        throw new AccessDeniedException(
	                "You are not authorized to update other users' procurement cases.");
	    }

	    if (procurementCase.getStatus() != ProcurementStatus.DRAFT) {
	        throw new InvalidInputException(
	                "Only procurement cases in DRAFT status can be updated.");
	    }

	    mapper.map(procurementRequestDto, procurementCase);

	    ProcurementCase updatedProcurementCase =
	            procurementCaseRepo.save(procurementCase);

	    return mapper.map(updatedProcurementCase,
	            ProcurementResponseDto.class);
	}

	@Override
	public ApiResponseDto deleteProcurementCase(Long csId) {

	    User currentUser = currentUserService.getCurrentUser();

	    ProcurementCase procurementCase = procurementCaseRepo.findById(csId)
	            .orElseThrow(() -> new ResourceNotFoundException("Invalid CS Id."));

	    accessValidator.validateUserAccess(procurementCase.getCreatedBy());

	    if (currentUser.getDesignation() == Designation.PROCUREMENT_EXECUTIVE
	            && !currentUser.getUserId().equals(procurementCase.getCreatedBy().getUserId())) {

	        throw new AccessDeniedException(
	                "You are not authorized to delete other users' procurement cases.");
	    }

	    if (procurementCase.getStatus() != ProcurementStatus.DRAFT) {
	        throw new InvalidInputException(
	                "Only procurement cases in DRAFT status can be deleted.");
	    }

	    procurementCase.setDeleted(true);
	    procurementCaseRepo.save(procurementCase);

	    return new ApiResponseDto("Procurement case deleted successfully.", "Success");
	}

}