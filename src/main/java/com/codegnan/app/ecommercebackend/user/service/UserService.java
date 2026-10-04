package com.codegnan.app.ecommercebackend.user.service;

import com.codegnan.app.ecommercebackend.user.dto.SignInRequestDto;
import com.codegnan.app.ecommercebackend.user.dto.SignUpRequestDto;
import com.codegnan.app.ecommercebackend.user.dto.UserResponseDto;

public interface UserService {
	void register(SignUpRequestDto signUpRequestDto);

	UserResponseDto login(SignInRequestDto signInRequestDto);

	String hashPassword(String password);
}
