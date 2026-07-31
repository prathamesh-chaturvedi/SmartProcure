package com.smartprocure.services;

import org.springframework.data.domain.Page;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.CompanyRequestDto;
import com.smartprocure.dtos.CompanyResponseDto;
import com.smartprocure.dtos.CompanyUpdateDto;


public interface CompanyService {

	CompanyResponseDto getCompany(Long companyId);

	Page<CompanyResponseDto> getCompanies(int page, int size, String address, String name);

	CompanyResponseDto addCompany(CompanyRequestDto companyRequestDto);
	
	CompanyResponseDto updateCompany(Long companyId, CompanyUpdateDto companyUpdateDto);

	ApiResponseDto deleteCompanyById(Long companyId);

	CompanyResponseDto getOwnCompany();

}
