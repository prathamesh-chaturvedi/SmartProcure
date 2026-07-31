package com.smartprocure.services.impl;

import java.math.BigDecimal;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartprocure.custom_exceptions.InvalidInputException;
import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.VendorQuoteRequestDto;
import com.smartprocure.dtos.VendorQuoteResponseDto;
import com.smartprocure.entities.Designation;
import com.smartprocure.entities.ProcurementCase;
import com.smartprocure.entities.ProcurementStatus;
import com.smartprocure.entities.VendorQuote;
import com.smartprocure.repositories.ProcurementCaseRepository;
import com.smartprocure.repositories.VendorQuoteRepository;
import com.smartprocure.security.AccessValidator;
import com.smartprocure.security.CurrentUserService;
import com.smartprocure.services.VendorQuoteService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class VendorQuoteServiceImpl implements VendorQuoteService {

	private final VendorQuoteRepository vendorQuoteRepository;
	private final ProcurementCaseRepository procurementCaseRepo;
	private final AccessValidator accessValidator;
	private final CurrentUserService currentUser;
	private final ModelMapper mapper;
	
	/**
	 * Retrieves a specific vendor quote for a procurement case after validating
	 * that the current user has access to the procurement case.
	 */
	@Override
	public VendorQuoteResponseDto getVendorQuote(Long quoteId, Long csId) {

	    ProcurementCase procurementCase = validateProcurementCaseAccess(csId);

	    VendorQuote vendorQuote = getVendorQuote(quoteId);

	    if (!vendorQuote.getProcurementCase().getProcurementCaseId()
	            .equals(procurementCase.getProcurementCaseId())) {

	        throw new InvalidInputException(
	                "Vendor quote does not belong to the specified procurement case.");
	    }

	    return mapToResponseDto(vendorQuote);
	}

	/**
	 * Retrieves all vendor quotations for a procurement case ordered by
	 * effective cost in ascending order.
	 */
	@Override
	public List<VendorQuoteResponseDto> getRankedVendorQuotes(Long csId) {

	    validateProcurementCaseAccess(csId);

	    return vendorQuoteRepository
	            .findByProcurementCaseProcurementCaseIdOrderByEffectiveCostAsc(csId)
	            .stream()
	            .map(this::mapToResponseDto)
	            .toList();
	}

	/**
	 * Adds a new vendor quotation to a procurement case after validating access
	 * and calculating quotation costs.
	 */
	@Override
	public VendorQuoteResponseDto addVendorQuote(VendorQuoteRequestDto dto) {

	    ProcurementCase procurementCase =
	            validateProcurementCaseForVendorQuote(dto.getProcurementCaseId());

	    VendorQuote vendorQuote = mapper.map(dto, VendorQuote.class);
	    vendorQuote.setProcurementCase(procurementCase);

	    calculateCosts(vendorQuote, procurementCase);

	    VendorQuote savedVendorQuote = vendorQuoteRepository.save(vendorQuote);

	    return mapToResponseDto(savedVendorQuote);
	}

	/**
	 * Updates an existing vendor quotation after validating access,
	 * ownership, and procurement case status.
	 */
	@Override
	public VendorQuoteResponseDto updateVendorQuote(Long quoteId,
	        VendorQuoteRequestDto dto) {

	    ProcurementCase procurementCase =
	            validateProcurementCaseForVendorQuote(dto.getProcurementCaseId());

	    VendorQuote vendorQuote = getVendorQuote(quoteId);

	    if (!vendorQuote.getProcurementCase().getProcurementCaseId()
	            .equals(procurementCase.getProcurementCaseId())) {

	        throw new InvalidInputException(
	                "Vendor quote does not belong to the specified procurement case.");
	    }

	    mapper.map(dto, vendorQuote);

	    calculateCosts(vendorQuote, procurementCase);

	    VendorQuote updatedVendorQuote = vendorQuoteRepository.save(vendorQuote);

	    return mapToResponseDto(updatedVendorQuote);
	}

	/**
	 * Deletes a vendor quotation after validating access and ensuring
	 * the procurement case is still in DRAFT status.
	 */
	@Override
	public ApiResponseDto deleteVendorQuote(Long quoteId) {

	    VendorQuote vendorQuote = getVendorQuote(quoteId);

	    validateProcurementCaseForVendorQuote(
	            vendorQuote.getProcurementCase().getProcurementCaseId());

	    vendorQuoteRepository.delete(vendorQuote);

	    return new ApiResponseDto(
	            "Vendor quote deleted successfully.",
	            "Success");
	}
	
	/**
	 * Retrieves a procurement case and validates that the current user
	 * has permission to access it.
	 */
	private ProcurementCase validateProcurementCaseAccess(Long procurementCaseId) {

	    ProcurementCase procurementCase = procurementCaseRepo.findById(procurementCaseId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Invalid Procurement Case Id"));

	    accessValidator.validateUserAccess(procurementCase.getCreatedBy());

	    if (currentUser.getCurrentUser().getDesignation() == Designation.PROCUREMENT_EXECUTIVE
	            && !currentUser.getCurrentUserId()
	                    .equals(procurementCase.getCreatedBy().getUserId())) {

	        throw new AccessDeniedException(
	                "You are not authorized to access other users' procurement cases.");
	    }

	    return procurementCase;
	}
	
	/**
	 * Validates that the procurement case is accessible and can still
	 * be modified by checking that it is in DRAFT status.
	 */
	private ProcurementCase validateProcurementCaseForVendorQuote(Long procurementCaseId) {

	    ProcurementCase procurementCase =
	            validateProcurementCaseAccess(procurementCaseId);

	    if (procurementCase.getStatus() != ProcurementStatus.DRAFT) {

	        throw new InvalidInputException(
	                "Vendor quotes can only be modified when the procurement case is in DRAFT status.");
	    }

	    return procurementCase;
	}
	
	/**
	 * Retrieves a vendor quotation by its ID.
	 */
	private VendorQuote getVendorQuote(Long quoteId) {

	    return vendorQuoteRepository.findById(quoteId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Invalid Vendor Quote Id"));
	}
	
	/**
	 * Calculates the quoted amount and effective cost of a vendor quotation.
	 */
	private void calculateCosts(VendorQuote vendorQuote,
	        ProcurementCase procurementCase) {

	    BigDecimal quotedAmount = vendorQuote.getQuotedRate()
	            .multiply(BigDecimal.valueOf(procurementCase.getQuantity()));

	    vendorQuote.setQuotedAmount(quotedAmount);

	    vendorQuote.setEffectiveCost(
	            quotedAmount.add(vendorQuote.getTransportationCost()));
	}
	
	
	/**
	 * Converts a VendorQuote entity into a VendorQuoteResponseDto.
	 */
	private VendorQuoteResponseDto mapToResponseDto(VendorQuote vendorQuote) {

	    VendorQuoteResponseDto response =
	            mapper.map(vendorQuote, VendorQuoteResponseDto.class);

	    response.setProcurementCaseId(
	            vendorQuote.getProcurementCase().getProcurementCaseId());

	    response.setProcurementCode(
	            vendorQuote.getProcurementCase().getProcurementCode());

	    return response;
	}

}
