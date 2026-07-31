package com.smartprocure.services.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.AuthResponseDto;
import com.smartprocure.dtos.ChangePasswordRequestDto;
import com.smartprocure.dtos.LoginRequestDto;
import com.smartprocure.entities.User;
import com.smartprocure.repositories.UserRepository;
import com.smartprocure.security.CustomUserDetailsImpl;
import com.smartprocure.security.JwtUtils;
import com.smartprocure.services.AuthService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authManager;
	private final JwtUtils jwtUtils;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public AuthResponseDto authenticateUser(LoginRequestDto loginRequest) {
		
		UsernamePasswordAuthenticationToken holder = new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());
		
		Authentication fullyAuthenticatedDetails = authManager.authenticate(holder);
		
		CustomUserDetailsImpl userDetails = (CustomUserDetailsImpl) fullyAuthenticatedDetails.getPrincipal();
		
		return new AuthResponseDto(jwtUtils.generateJwt(userDetails),
									userDetails.getUser().getUserId(),
									userDetails.getUser().getFirstName(),
									userDetails.getUser().getUserRole(),
									userDetails.getUser().getCompany().getCompanyId());
	}

	@Override
	public ApiResponseDto changePassword(ChangePasswordRequestDto passwordRequest) {

	    // Verify email and current password
	    authManager.authenticate(
	            new UsernamePasswordAuthenticationToken(
	                    passwordRequest.getEmail(),
	                    passwordRequest.getPassword()));

	    User user = userRepository.findByEmail(passwordRequest.getEmail())
	            .orElseThrow(() -> new ResourceNotFoundException("User not found."));

	    user.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));

	    return new ApiResponseDto(
	            "Password changed successfully.",
	            "Success");
	}

}
