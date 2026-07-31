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
import com.smartprocure.dtos.UserRequestDto;
import com.smartprocure.dtos.UserResponseDto;
import com.smartprocure.dtos.UserUpdateDto;
import com.smartprocure.entities.Designation;
import com.smartprocure.entities.UserRole;
import com.smartprocure.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
	
	private final UserService userService;
	
	
	@GetMapping()
	public ResponseEntity<UserResponseDto> getUser(@RequestParam(required = false) Long userId)
	{
		System.out.println("controller");
		return ResponseEntity.ok(userService.getUser(userId));
	}
	
	
	@GetMapping("/list")
	public ResponseEntity<Page<UserResponseDto>> getUsers(
			@RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size,
	        @RequestParam(required = false) String name,
			@RequestParam(required = false) UserRole userRole,
			@RequestParam(required = false) Designation designation,
			@RequestParam(required = false) Boolean isActive)
	{
		return ResponseEntity.ok(
				userService.getUsers(page, size, name, userRole, designation, isActive));
	}
	
	
	
	@PostMapping
	public ResponseEntity<UserResponseDto> addUser(@RequestBody @Valid UserRequestDto userRequestDto)
	{
	
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.addUser(userRequestDto));
	}
	
	
	@PutMapping()
	public ResponseEntity<UserResponseDto> updateUser(
					@RequestParam(required = false) Long userId,
					@RequestBody @Valid UserUpdateDto userUpdateDto)
	{
		return ResponseEntity.ok(userService.updateUser(userId, userUpdateDto));
	}
	
	
	
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponseDto> deleteUser(@PathVariable Long userId) 
	{
		return ResponseEntity.ok(userService.deleteUser(userId));
	}
	
	
	
}
