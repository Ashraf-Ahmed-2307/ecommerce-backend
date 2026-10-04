package com.codegnan.app.ecommercebackend.user.dao;

import java.time.LocalDateTime;

import com.codegnan.app.ecommercebackend.user.dto.CredentialDto;
import com.codegnan.app.ecommercebackend.user.dto.SignUpRequestDto;

public interface UserDao {
	Long save(SignUpRequestDto signUpRequestDto, String passwordHash);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

	CredentialDto findByUsername(String username);

	boolean recordFailedLogin(long credentialId, int maxAttempts, LocalDateTime lockedUntil);

	boolean recordSuccessfulLogin(long credentialId);
}
