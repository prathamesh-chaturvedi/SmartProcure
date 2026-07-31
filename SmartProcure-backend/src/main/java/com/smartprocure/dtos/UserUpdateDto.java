package com.smartprocure.dtos;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserUpdateDto {

    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @Email(message = "Invalid email format")
    private String email;
}