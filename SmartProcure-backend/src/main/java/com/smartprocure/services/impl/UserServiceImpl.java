package com.smartprocure.services.impl;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartprocure.custom_exceptions.DuplicateResourceException;
import com.smartprocure.custom_exceptions.InvalidInputException;
import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.UserRequestDto;
import com.smartprocure.dtos.UserResponseDto;
import com.smartprocure.dtos.UserUpdateDto;
import com.smartprocure.entities.Company;
import com.smartprocure.entities.Designation;
import com.smartprocure.entities.User;
import com.smartprocure.entities.UserRole;
import com.smartprocure.repositories.CompanyRepository;
import com.smartprocure.repositories.UserRepository;
import com.smartprocure.security.AccessValidator;
import com.smartprocure.security.CurrentUserService;
import com.smartprocure.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final AccessValidator accessValidator;

    
    //check company id maping
    @Override
    public UserResponseDto getUser(Long userId) {

        if (userId == null) {
            userId = currentUserService.getCurrentUserId();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User Id"));

        accessValidator.validateActiveUser();
        accessValidator.validateUserAccess(user);

        UserResponseDto userResponse =  mapper.map(user, UserResponseDto.class);
        userResponse.setCompanyId(user.getCompany().getCompanyId());
        userResponse.setCompanyName(user.getCompany().getCompanyName());
        
        return userResponse;
    }

    @Override
    public Page<UserResponseDto> getUsers(int page, int size, String name,
            UserRole userRole, Designation designation, Boolean isActive) {

    	
    	accessValidator.validateActiveUser();
    	
        Pageable pageable = PageRequest.of(page, size);

        Long companyId = currentUserService.getCurrentUserCompanyId();

        return userRepository
                .getUsers(name, companyId, userRole, designation, isActive, pageable)
                .map(user -> {
                    UserResponseDto userResponse = mapper.map(user, UserResponseDto.class);
                    userResponse.setCompanyId(user.getCompany().getCompanyId());
                    userResponse.setCompanyName(user.getCompany().getCompanyName());
                    return userResponse;
                });
    }

    @Override
    public UserResponseDto addUser(UserRequestDto userRequestDto) {

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists.");
        }
        
        accessValidator.validateActiveUser();
        
        Long companyId = currentUserService.getCurrentUserCompanyId();

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found."));

        User user = mapper.map(userRequestDto, User.class);
        user.setCompany(company);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));

        User savedUser = userRepository.save(user);

        UserResponseDto response = mapper.map(savedUser, UserResponseDto.class);
        response.setCompanyId(company.getCompanyId());
        response.setCompanyName(company.getCompanyName());

        return response;
    }

    @Override
    public UserResponseDto createCompanyAdmin(UserRequestDto userRequestDto, Long companyId) {

        if (userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new DuplicateResourceException("Email already exists.");
        }

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found."));

        if (!company.isActive()) {
            throw new InvalidInputException("Cannot create an admin for an inactive company.");
        }
        
        User user = mapper.map(userRequestDto, User.class);
        user.setCompany(company);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setUserRole(UserRole.ADMIN);

        User savedUser = userRepository.save(user);

        UserResponseDto response = mapper.map(savedUser, UserResponseDto.class);
        response.setCompanyId(company.getCompanyId());
        response.setCompanyName(company.getCompanyName());

        return response;
    }

    @Override
    public ApiResponseDto deleteUser(Long userId) {

    	accessValidator.validateActiveUser();
    	
        if (userId.equals(currentUserService.getCurrentUserId())) {
            throw new InvalidInputException("You cannot delete your own account.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User Id"));

        accessValidator.validateUserAccess(user);

        user.setActive(false);

        return new ApiResponseDto("User deleted successfully.", "Success");
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserUpdateDto userUpdateDto) {

        accessValidator.validateActiveUser();

        if (userId == null) {
            userId = currentUserService.getCurrentUserId();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid User Id"));

        accessValidator.validateUserAccess(user);

        mapper.map(userUpdateDto, user);

        UserResponseDto userResponse = mapper.map(user, UserResponseDto.class);

        userResponse.setCompanyId(user.getCompany().getCompanyId());
        userResponse.setCompanyName(user.getCompany().getCompanyName());

        return userResponse;
    }


}