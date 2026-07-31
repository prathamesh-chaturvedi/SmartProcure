package com.smartprocure.security;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtPrincipal {

	private Long userId;
	private Long companyId;
}

