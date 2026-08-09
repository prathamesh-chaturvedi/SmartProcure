package com.smartprocure.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartprocure.custom_exceptions.InvalidInputException;
import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ApprovalDecisionDto;
import com.smartprocure.dtos.ApprovalHistoryResponseDto;
import com.smartprocure.dtos.ProcurementResponseDto;
import com.smartprocure.entities.Action;
import com.smartprocure.entities.ApprovalHistory;
import com.smartprocure.entities.ApprovalMatrix;
import com.smartprocure.entities.Designation;
import com.smartprocure.entities.ProcurementCase;
import com.smartprocure.entities.ProcurementStatus;
import com.smartprocure.entities.VendorQuote;
import com.smartprocure.repositories.ApprovalHistoryRepository;
import com.smartprocure.repositories.ApprovalMatrixRepository;
import com.smartprocure.repositories.ProcurementCaseRepository;
import com.smartprocure.repositories.VendorQuoteRepository;
import com.smartprocure.security.AccessValidator;
import com.smartprocure.security.CurrentUserService;
import com.smartprocure.services.ApprovalHistoryService;
import com.smartprocure.services.EmailService;
import com.smartprocure.services.PdfService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ApprovalHistoryServiceImpl implements ApprovalHistoryService {

	private final ApprovalHistoryRepository approvalHistoryRepo;
	private final ProcurementCaseRepository procurementCaseRepo;
	private final ApprovalMatrixRepository approvalMatrixRepo;
	private final VendorQuoteRepository vendorQuoteRepo;

	private final AccessValidator accessValidator;
	private final CurrentUserService currentUser;

	private final EmailService emailService;
	private final PdfService pdfService;

	private final ModelMapper mapper;

	@Override
	public List<ProcurementResponseDto> getPendingProcurementCases() {

	    accessValidator.validateActiveUser();

	    List<ApprovalHistory> approvalHistoryList =
	            approvalHistoryRepo.findByApproverUserIdAndAction(
	                    currentUser.getCurrentUserId(),
	                    Action.PENDING);

	    return approvalHistoryList.stream()
	            .map(approvalHistory ->
	                    mapper.map(
	                            approvalHistory.getProcurementCase(),
	                            ProcurementResponseDto.class))
	            .toList();
	}


	@Override
	public ApiResponseDto submitProcurementCase(Long csId) {

	    ProcurementCase procurementCase =
	            validateProcurementCaseForSubmission(csId);

	    updatePackageAmountAndRecommendedVendor(procurementCase);

	    ApprovalMatrix approvalMatrix =
	            getApprovalMatrix(procurementCase, 1);

	    createApprovalHistory(
	            procurementCase,
	            approvalMatrix,
	            procurementCase.getDraftNumber());

	    procurementCase.setStatus(ProcurementStatus.UNDER_REVIEW);

	    emailService.sendReviewEmail(
	            procurementCase,
	            approvalMatrix.getApprover());

	    return new ApiResponseDto(
	            "Procurement case submitted successfully.",
	            "Success");
	}


	@Override
	public ApiResponseDto approveProcurementCase(Long csId, 
			 ApprovalDecisionDto approvalDecisionDto) {

	    ApprovalHistory currentApproval =
	            getPendingApprovalHistory(csId);

	    validateCurrentApprover(currentApproval);

	    currentApproval.setRemarks(
	            approvalDecisionDto.getRemarks());
	    currentApproval.setAction(Action.APPROVED);

	    ApprovalMatrix nextApprovalMatrix =
	            findNextApprovalMatrix(
	                    currentApproval.getProcurementCase(),
	                    currentApproval.getApprovalLevel() + 1);

	    if (nextApprovalMatrix != null) {

	    	createApprovalHistory(
	    	        currentApproval.getProcurementCase(),
	    	        nextApprovalMatrix,
	    	        currentApproval.getApprovalCycle());

	        emailService.sendReviewEmail(
	                currentApproval.getProcurementCase(),
	                nextApprovalMatrix.getApprover());

	    } else {

	        ProcurementCase procurementCase =
	                currentApproval.getProcurementCase();

	        procurementCase.setStatus(
	                ProcurementStatus.APPROVED);

	     // TODO:
	     // Generate Comparative Statement PDF,
	     // store its path in ProcurementCase,
	     // then email the creator with the PDF attached.
	        pdfService.generateComparativeStatementPdf(
	                procurementCase.getProcurementCaseId());

	        emailService.sendApprovalEmail(procurementCase);
	    }

	    return new ApiResponseDto(
	            "Procurement case approved successfully.",
	            "Success");
	}

	@Override
	public ApiResponseDto rejectProcurementCase(Long csId,
			ApprovalDecisionDto approvalDecisionDto) {

	    ApprovalHistory currentApproval =
	            getPendingApprovalHistory(csId);

	    validateCurrentApprover(currentApproval);

	    currentApproval.setRemarks(
	            approvalDecisionDto.getRemarks());
	    currentApproval.setAction(Action.REJECTED);

	    ProcurementCase procurementCase =
	            currentApproval.getProcurementCase();

	    procurementCase.setStatus(ProcurementStatus.DRAFT);
	    procurementCase.setDraftNumber(
	            procurementCase.getDraftNumber() + 1);

	    emailService.sendRejectionEmail(procurementCase);

	    return new ApiResponseDto(
	            "Procurement case rejected successfully.",
	            "Success");
	}
	
	
	@Override
	public List<ApprovalHistoryResponseDto> getApprovalHistory(Long csId) {

	    ProcurementCase procurementCase = procurementCaseRepo.findById(csId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Invalid Procurement Case Id"));

	    accessValidator.validateUserAccess(procurementCase.getCreatedBy());
	    
	    if (currentUser.getCurrentUser().getDesignation() == Designation.PROCUREMENT_EXECUTIVE
	            && !currentUser.getCurrentUserId()
	                    .equals(procurementCase.getCreatedBy().getUserId())) {

	        throw new AccessDeniedException(
	                "You are not authorized to view the approval history of other users' procurement cases.");
	    }

	    return approvalHistoryRepo
	            .findByProcurementCaseProcurementCaseIdOrderByApprovalCycleAscApprovalLevelAsc(csId)
	            .stream()
	            .map(approvalHistory -> {

	                ApprovalHistoryResponseDto response =
	                        mapper.map(approvalHistory, ApprovalHistoryResponseDto.class);

	                response.setApproverName(
	                        approvalHistory.getApprover().getFirstName()
	                        + " "
	                        + approvalHistory.getApprover().getLastName());

	                return response;
	            })
	            .toList();
	}
	
	/*
	 * Validates whether the logged-in user is authorized
	 * to approve the current procurement case.
	 */
	private void validateCurrentApprover(ApprovalHistory approvalHistory) {

	    accessValidator.validateActiveUser();

	    if (!approvalHistory.getApprover().getUserId()
	            .equals(currentUser.getCurrentUserId())) {

	        throw new AccessDeniedException(
	                "You are not authorized to approve this procurement case.");
	    }
	}
	
	/*
	 * Returns the approval matrix for the specified
	 * approval level and procurement amount.
	 */
	private ApprovalMatrix getApprovalMatrix(
	        ProcurementCase procurementCase,
	        Integer approvalLevel) {

	    List<ApprovalMatrix> approvalMatrixList =
	            approvalMatrixRepo.findByCompanyCompanyId(
	                    procurementCase.getCreatedBy()
	                            .getCompany()
	                            .getCompanyId());

	    return approvalMatrixList.stream()

	            .filter(matrix ->
	                    matrix.getApprovalLevel().equals(approvalLevel))

	            .filter(matrix ->
	                    procurementCase.getPackageAmount()
	                            .compareTo(matrix.getMinAmount()) >= 0
	                    &&
	                    procurementCase.getPackageAmount()
	                            .compareTo(matrix.getMaxAmount()) <= 0)

	            .findFirst()

	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "No approval matrix found for the specified approval level and package amount."));
	}
	
	/*
	 * Returns the next approval matrix if available.
	 * Returns null when no further approval level exists.
	 */
	private ApprovalMatrix findNextApprovalMatrix(
	        ProcurementCase procurementCase,
	        Integer approvalLevel) {

	    List<ApprovalMatrix> approvalMatrixList =
	            approvalMatrixRepo.findByCompanyCompanyId(
	                    procurementCase.getCreatedBy()
	                            .getCompany()
	                            .getCompanyId());

	    return approvalMatrixList.stream()

	            .filter(matrix ->
	                    matrix.getApprovalLevel().equals(approvalLevel))

	            .filter(matrix ->
	                    procurementCase.getPackageAmount()
	                            .compareTo(matrix.getMinAmount()) >= 0
	                    &&
	                    procurementCase.getPackageAmount()
	                            .compareTo(matrix.getMaxAmount()) <= 0)

	            .findFirst()

	            .orElse(null);
	}
	
	/*
	 * Returns the current pending approval history
	 * for the specified procurement case.
	 */
	private ApprovalHistory getPendingApprovalHistory(Long csId) {

	    return approvalHistoryRepo
	            .findFirstByProcurementCaseProcurementCaseIdAndAction(
	                    csId,
	                    Action.PENDING)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException(
	                            "No pending approval found for the specified procurement case."));
	}
	
	/*
	 * Creates the approval history record.
	 */
	private void createApprovalHistory(
	        ProcurementCase procurementCase,
	        ApprovalMatrix approvalMatrix,
	        Integer approvalCycle) {

	    ApprovalHistory approvalHistory = new ApprovalHistory();

	    approvalHistory.setApprovalCycle(approvalCycle);
	    approvalHistory.setApprovalLevel(approvalMatrix.getApprovalLevel());
	    approvalHistory.setAction(Action.PENDING);
	    approvalHistory.setApprover(approvalMatrix.getApprover());
	    approvalHistory.setProcurementCase(procurementCase);

	    approvalHistoryRepo.save(approvalHistory);
	}
	
	/*
	 * Validates whether the procurement case is eligible for submission.
	 */
	private ProcurementCase validateProcurementCaseForSubmission(Long csId) {

	    ProcurementCase procurementCase = procurementCaseRepo.findById(csId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Invalid Procurement Case Id"));

	    accessValidator.validateUserAccess(procurementCase.getCreatedBy());

	    if (!currentUser.getCurrentUserId()
	            .equals(procurementCase.getCreatedBy().getUserId())) {

	        throw new AccessDeniedException(
	                "You are not authorized to submit this procurement case.");
	    }

	    if (procurementCase.getStatus() != ProcurementStatus.DRAFT) {

	        throw new InvalidInputException(
	                "Only procurement cases in DRAFT status can be submitted.");
	    }

	    return procurementCase;
	}
	
	

	/*
	 * Calculates the package amount and recommended vendor
	 * using the lowest effective cost vendor.
	 */
	private void updatePackageAmountAndRecommendedVendor(
	        ProcurementCase procurementCase) {

	    List<VendorQuote> vendorQuotes = vendorQuoteRepo
	            .findByProcurementCaseProcurementCaseIdOrderByEffectiveCostAsc(
	                    procurementCase.getProcurementCaseId());

	    if (vendorQuotes.isEmpty()) {

	        throw new InvalidInputException(
	                "At least one vendor quotation is required before submission.");
	    }

	    VendorQuote l1Vendor = vendorQuotes.get(0);

	    procurementCase.setPackageAmount(
	            l1Vendor.getEffectiveCost());

	    procurementCase.setRecommendedVendor(
	            l1Vendor.getVendorName());
	}


	

}
