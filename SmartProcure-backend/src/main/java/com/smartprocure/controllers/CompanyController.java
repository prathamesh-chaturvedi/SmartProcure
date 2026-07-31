package com.smartprocure.controllers;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.CompanyRequestDto;
import com.smartprocure.dtos.CompanyResponseDto;
import com.smartprocure.dtos.CompanyUpdateDto;
import com.smartprocure.dtos.UserRequestDto;
import com.smartprocure.dtos.UserResponseDto;
import com.smartprocure.services.CompanyService;
import com.smartprocure.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
public class CompanyController {
	
	private final CompanyService companyService;
	private final UserService userService;
	
	
	@GetMapping("/{companyId}")
	public ResponseEntity<CompanyResponseDto> getCompany(@PathVariable Long companyId)
	{
		return ResponseEntity.ok(companyService.getCompany(companyId));
	}
	
	@GetMapping("/me")
	public ResponseEntity<CompanyResponseDto> getOwnCompany()
	{
		return ResponseEntity.ok(companyService.getOwnCompany());
	}
	
	
	@GetMapping
	public ResponseEntity<Page<CompanyResponseDto>> getCompanies(
			@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String address,
			@RequestParam(required = false) String name)
	{
		return ResponseEntity.ok(companyService.getCompanies(page, size, address, name));
	}
	
	
	@PostMapping
	public ResponseEntity<CompanyResponseDto> addCompany
				(@RequestBody @Valid CompanyRequestDto companyRequestDto)
	{
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(companyService.addCompany(companyRequestDto));
	}	
	
	@PostMapping("/{companyId}/admin")
	public ResponseEntity<UserResponseDto> createCompanyAdmin
							(@RequestBody @Valid UserRequestDto userRequestDto, 
									@PathVariable Long companyId)
	{
	
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(userService.createCompanyAdmin(userRequestDto, companyId));
	}
	
	@PutMapping("/{companyId}")
	public ResponseEntity<CompanyResponseDto> updateCompany
							(@PathVariable Long companyId, @RequestBody @Valid CompanyUpdateDto companyUpdateDto)
	{
		return ResponseEntity.ok(companyService
				.updateCompany(companyId, companyUpdateDto));
	}
	
	
	@DeleteMapping("/{companyId}")
	public ResponseEntity<ApiResponseDto> deleteCompany(@PathVariable Long companyId)
	{
		return ResponseEntity.ok(companyService.deleteCompanyById(companyId));
	}
	
	
	
}
