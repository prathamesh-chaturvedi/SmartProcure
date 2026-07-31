package com.smartprocure.exception_handler;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.smartprocure.custom_exceptions.DuplicateResourceException;
import com.smartprocure.custom_exceptions.ResourceNotFoundException;
import com.smartprocure.dtos.ApiResponseDto;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDto> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponseDto(e.getMessage(), "Failed"));
    }

    // Handle Duplicate Resource
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException e) {

        String message = "Resource already exists.";

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDto(message, "Failed"));
    }
    
    // Handle Authentication Errors (Invalid credentials)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponseDto> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponseDto(e.getMessage(), "Failed"));
    }
    
    // Handle Duplicate Email
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponseDto> handleDuplicateResourceException(
            DuplicateResourceException e) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponseDto(e.getMessage(), "Failed"));
    }

    // Handle Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();

        return fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage));
    }

    // Catch-all Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDto> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponseDto(e.getMessage(), "Failed"));
    }
}