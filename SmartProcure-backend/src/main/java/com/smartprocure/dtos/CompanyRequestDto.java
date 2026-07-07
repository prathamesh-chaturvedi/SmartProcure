package com.smartprocure.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyRequestDto {

    @NotBlank(message = "Company name is required")
    @Size(max = 50, message = "Company name cannot exceed 50 characters")
    private String companyName;

    @NotBlank(message = "Company address is required")
    @Size(max = 255, message = "Company address cannot exceed 255 characters")
    private String address;

    @NotBlank(message = "Company email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[0-9]{10,14}$", message = "Phone number must contain 10 to 14 digits")
    private String phone;
}