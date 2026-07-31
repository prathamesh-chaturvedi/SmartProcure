package com.smartprocure.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.AuthResponseDto;
import com.smartprocure.dtos.ChangePasswordRequestDto;
import com.smartprocure.dtos.LoginRequestDto;
import com.smartprocure.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final AuthService authService;
	
	@PostMapping("/login")
	public ResponseEntity<AuthResponseDto> userLogin(@Valid @RequestBody LoginRequestDto loginRequestDto)
	{
		
		return ResponseEntity.ok(authService.authenticateUser(loginRequestDto));
		
	}
	
	@PatchMapping("/change-password")
	public ResponseEntity<ApiResponseDto> changePassword(@Valid @RequestBody ChangePasswordRequestDto passwordChange)
	{
		return ResponseEntity.ok(authService.changePassword(passwordChange));
	}
	
}
