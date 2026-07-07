package com.smartprocure.dtos;

import com.smartprocure.entities.UserRole;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponseDto {

    private String token;

    private Long userId;

    private String firstName;

    private UserRole userRole;

    private Long companyId;
}
