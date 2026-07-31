package com.smartprocure.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartprocure.custom_exceptions.InvalidInputException;
import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.CompanyRequestDto;
import com.smartprocure.dtos.CompanyResponseDto;
import com.smartprocure.dtos.CompanyUpdateDto;
import com.smartprocure.entities.Company;
import com.smartprocure.entities.User;
import com.smartprocure.repositories.CompanyRepository;
import com.smartprocure.repositories.UserRepository;
import com.smartprocure.security.AccessValidator;
import com.smartprocure.security.CurrentUserService;
import com.smartprocure.services.CompanyService;

import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

	private final ModelMapper mapper;
	private final CompanyRepository companyRepository;
	private final UserRepository userRepository;
	private final CurrentUserService currentUserService;
	private final AccessValidator accessValidator;
	
	@Override
	public CompanyResponseDto getCompany(Long companyId) {
		
		Company company = companyRepository.findById(companyId)
				.orElseThrow(()-> new ResourceNotFoundException("Company not found."));
		
		return mapper.map(company, CompanyResponseDto.class);
	}
	
	@Override
	public CompanyResponseDto getOwnCompany() {
		
		accessValidator.validateActiveUser();
		Company company = companyRepository.findById(currentUserService.getCurrentUserCompanyId())
				.orElseThrow(()-> new ResourceNotFoundException("Company not found."));
		
		return mapper.map(company, CompanyResponseDto.class);
	}

	@Override
	public Page<CompanyResponseDto> getCompanies(int page, int size, String address, String name) {
		
		Pageable pageable = PageRequest.of(page, size);
		
		return companyRepository.searchCompanies(name, address, pageable)
				.map(company -> mapper.map(company, CompanyResponseDto.class));
	}

	@Override
	public CompanyResponseDto addCompany(CompanyRequestDto companyRequestDto) {
		Company company = mapper.map(companyRequestDto, Company.class);
		Company savedCompany = companyRepository.save(company);

		return mapper.map(savedCompany, CompanyResponseDto.class);
	}
	

	@Override
	public CompanyResponseDto updateCompany(Long companyId, CompanyUpdateDto companyRequestDto) {
		
		Company company = companyRepository.findById(companyId)
				.orElseThrow(()-> new ResourceNotFoundException("Company not found."));
		
		mapper.map(companyRequestDto, company);
		
		return mapper.map(company, CompanyResponseDto.class);
	}

	@Override
	public ApiResponseDto deleteCompanyById(Long companyId) {

		if (companyId.equals(1L)) {
		    throw new InvalidInputException("Platform company cannot be deleted.");
		}
		
		Company company = companyRepository.findById(companyId)
				.orElseThrow(()-> new ResourceNotFoundException("Company not found."));
		
		company.setActive(false);
		
		List<User> users = userRepository.findByCompanyCompanyId(companyId);
		
		users.forEach(user -> user.setActive(false));
		
		return new ApiResponseDto("Company deleted successfully.", "Success");
	}

	

}
