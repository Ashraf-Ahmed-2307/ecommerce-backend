package com.codegnan.app.ecommercebackend.user.dto;

import java.time.LocalDateTime;

public record CredentialDto(Long credentialId, String username, String passwordHash, boolean enabled,
		int failedLoginAttempts, LocalDateTime lockedUntil, UserResponseDto user, boolean userActive) {
}
