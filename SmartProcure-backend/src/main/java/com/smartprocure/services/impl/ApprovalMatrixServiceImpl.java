package com.smartprocure.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartprocure.custom_exceptions.InvalidInputException;
import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.ApprovalMatrixRequestDto;
import com.smartprocure.dtos.ApprovalMatrixResponseDto;
import com.smartprocure.entities.ApprovalMatrix;
import com.smartprocure.entities.Company;
import com.smartprocure.entities.User;
import com.smartprocure.repositories.ApprovalMatrixRepository;
import com.smartprocure.repositories.CompanyRepository;
import com.smartprocure.repositories.UserRepository;
import com.smartprocure.security.AccessValidator;
import com.smartprocure.services.ApprovalMatrixService;

import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class ApprovalMatrixServiceImpl implements ApprovalMatrixService {

	private final ApprovalMatrixRepository approvalMatrixRepository;
	private final AccessValidator accessValidator;
	private final ModelMapper mapper;
	private final CompanyRepository companyRepository;
	private final UserRepository userRepository;
	
	@Override
	public List<ApprovalMatrixResponseDto> getApprovalMatrices(Long companyId) {

	    accessValidator.validateActiveUser();

	    validateCompany(companyId);

	    return approvalMatrixRepository.findByCompanyCompanyId(companyId)
	            .stream()
	            .map(this::mapToResponseDto)
	            .toList();
	}

	@Override
	public ApprovalMatrixResponseDto getApprovalMatrix(Long matrixId) {

	    accessValidator.validateActiveUser();

	    ApprovalMatrix approvalMatrix = getMatrix(matrixId);

	    accessValidator.validateUserAccess(
	            approvalMatrix.getCompany().getCompanyId());

	    return mapToResponseDto(approvalMatrix);
	}

	@Override
	public ApprovalMatrixResponseDto addApprovalMatrix(
	        ApprovalMatrixRequestDto dto) {

	    Company company = validateCompany(dto.getCompanyId());

	    User approver = validateApprover(
	            dto.getApproverId(), company);

	    ApprovalMatrix approvalMatrix =
	            mapper.map(dto, ApprovalMatrix.class);

	    approvalMatrix.setCompany(company);
	    approvalMatrix.setApprover(approver);

	    return mapToResponseDto(
	            approvalMatrixRepository.save(approvalMatrix));
	}

	@Override
	public ApprovalMatrixResponseDto updateApprovalMatrix(
	        Long matrixId,
	        ApprovalMatrixRequestDto dto) {

	    ApprovalMatrix approvalMatrix = getMatrix(matrixId);

	    accessValidator.validateUserAccess(
	            approvalMatrix.getCompany().getCompanyId());

	    Company company = validateCompany(dto.getCompanyId());

	    User approver = validateApprover(
	            dto.getApproverId(), company);

	    mapper.map(dto, approvalMatrix);

	    approvalMatrix.setCompany(company);
	    approvalMatrix.setApprover(approver);

	    return mapToResponseDto(
	            approvalMatrixRepository.save(approvalMatrix));
	}

	@Override
	public ApiResponseDto deleteApprovalMatrix(Long matrixId) {

	    ApprovalMatrix approvalMatrix = getMatrix(matrixId);

	    accessValidator.validateUserAccess(
	            approvalMatrix.getCompany().getCompanyId());

	    approvalMatrixRepository.delete(approvalMatrix);

	    return new ApiResponseDto(
	            "Approval matrix deleted successfully.",
	            "Success");
	}
	
	private ApprovalMatrix getMatrix(Long matrixId) {

	    return approvalMatrixRepository.findById(matrixId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Invalid Approval Matrix Id"));
	}
	
	
	private Company validateCompany(Long companyId) {

	    accessValidator.validateUserAccess(companyId);

	    return companyRepository.findById(companyId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Invalid Company Id"));
	}
	
	private User validateApprover(Long approverId, Company company) {

	    User approver = userRepository.findById(approverId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Invalid Approver Id"));

	    if (!approver.getCompany().getCompanyId()
	            .equals(company.getCompanyId())) {

	        throw new InvalidInputException(
	                "The selected approver does not belong to the specified company.");
	    }

	    return approver;
	}
	
	private ApprovalMatrixResponseDto mapToResponseDto(
	        ApprovalMatrix approvalMatrix) {

	    ApprovalMatrixResponseDto response =
	            mapper.map(approvalMatrix, ApprovalMatrixResponseDto.class);

	    response.setApproverId(
	            approvalMatrix.getApprover().getUserId());

	    response.setApproverName(
	            approvalMatrix.getApprover().getFirstName() + " "
	                    + approvalMatrix.getApprover().getLastName());

	    response.setCompanyId(
	            approvalMatrix.getCompany().getCompanyId());

	    response.setCompanyName(
	            approvalMatrix.getCompany().getCompanyName());

	    return response;
	}

}
