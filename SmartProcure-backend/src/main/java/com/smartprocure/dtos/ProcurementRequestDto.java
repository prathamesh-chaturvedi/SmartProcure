package com.smartprocure.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProcurementRequestDto {

	@NotBlank(message = "Title is required")
	@Size(max = 50, message = "Title cannot exceed 50 characters")
	private String title;

	@NotBlank(message = "Description is required")
	@Size(max = 250, message = "Description cannot exceed 250 characters")
	private String description;

	@NotBlank(message = "Unit is required")
	@Size(max = 50, message = "Unit cannot exceed 50 characters")
	private String unit;

	@NotNull(message = "Quantity is required")
	@Min(value = 1, message = "Quantity must be greater than 0")
	private Integer quantity;

}
