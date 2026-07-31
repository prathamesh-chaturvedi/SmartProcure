package com.smartprocure.services;

import org.springframework.data.domain.Page;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.UserRequestDto;
import com.smartprocure.dtos.UserResponseDto;
import com.smartprocure.dtos.UserUpdateDto;
import com.smartprocure.entities.Designation;
import com.smartprocure.entities.UserRole;

public interface UserService {

	UserResponseDto getUser(Long userId);

	Page<UserResponseDto> getUsers(int page, int size, String name, UserRole userRole, Designation designation, Boolean isActive);

	UserResponseDto addUser(UserRequestDto userRequestDto);
	
	UserResponseDto createCompanyAdmin(UserRequestDto userRequestDto, Long companyId);

	ApiResponseDto deleteUser(Long userId);

	UserResponseDto updateUser(Long userId, UserUpdateDto userUpdateDto);

	

	
}
