package com.smartprocure.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CompanyResponseDto {

    private Long companyId;

    private String companyName;

    private String address;

    private String email;

    private String phone;

    private boolean isActive;
}