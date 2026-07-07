package com.smartprocure.services;

import org.springframework.data.domain.Page;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.CompanyRequestDto;
import com.smartprocure.dtos.CompanyResponseDto;


public interface CompanyService {

	CompanyResponseDto getCompany(Long companyId);

	Page<CompanyResponseDto> getCompanies(int page, int size, String location, String name);

	CompanyResponseDto addCompany(CompanyRequestDto companyRequestDto);

	CompanyResponseDto updateCompany(Long companyId, CompanyRequestDto companyRequestDto);

	ApiResponseDto deleteCompanyById(Long companyId);

}
