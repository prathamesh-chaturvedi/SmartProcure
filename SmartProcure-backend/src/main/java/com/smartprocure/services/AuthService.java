package com.smartprocure.services;

import com.smartprocure.dtos.ApiResponseDto;
import com.smartprocure.dtos.AuthResponseDto;
import com.smartprocure.dtos.ChangePasswordRequestDto;
import com.smartprocure.dtos.LoginRequestDto;

public interface AuthService {

	AuthResponseDto authenticateUser(LoginRequestDto loginRequestDto);

	ApiResponseDto changePassword(ChangePasswordRequestDto passwordDto);

}
