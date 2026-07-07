package com.smartprocure.dtos;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseDto {
	
	private String message;
	private String status;
	private LocalDateTime timeStamp;

	public ApiResponseDto(String message, String status) {
		this.message = message;
		this.status = status;
		this.timeStamp = LocalDateTime.now();
	}
	
	
}
