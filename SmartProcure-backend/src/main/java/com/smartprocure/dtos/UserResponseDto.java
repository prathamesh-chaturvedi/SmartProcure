package com.smartprocure.dtos;

import java.time.LocalDate;

import com.smartprocure.entities.Designation;
import com.smartprocure.entities.UserRole;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseDto {

    private Long userId;

    private String firstName;

    private String lastName;

    private LocalDate dob;

    private String email;

    private UserRole userRole;

    private Designation designation;

    private boolean isActive;

    private Long companyId;

    private String companyName;
}