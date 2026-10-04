package com.codegnan.app.ecommercebackend.user.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codegnan.app.ecommercebackend.user.dao.UserDao;
import com.codegnan.app.ecommercebackend.user.dto.SignInRequestDto;
import com.codegnan.app.ecommercebackend.user.dto.SignUpRequestDto;
import com.codegnan.app.ecommercebackend.user.dto.UserResponseDto;

import at.favre.lib.crypto.bcrypt.BCrypt;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	private static final int BCRYPT_COST = 10;
	private static final int MAX_FAILED_ATTEMPTS = 5;
	private static final int LOCK_MINUTES = 15;

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-z0-9._-]{3,100}$");

	private UserDao userDao;

	@Autowired
	public UserServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public void register(SignUpRequestDto signUpRequestDto) {
		if (signUpRequestDto == null) {
			throw new IllegalArgumentException("Registration details are required.");
		}

		var firstName = trim(signUpRequestDto.firstName());
		var lastName = trim(signUpRequestDto.lastName());
		var email = normalize(signUpRequestDto.email());
		var username = normalize(signUpRequestDto.username());
		var password = signUpRequestDto.password();

		if (firstName.isEmpty() || firstName.length() > 60) {
			throw new IllegalArgumentException("First name is required (1 to 60 characters).");
		}

		if (lastName.isEmpty() || lastName.length() > 60) {
			throw new IllegalArgumentException("Last name is required (1 to 60 characters).");
		}

		if (email.length() > 254 || !EMAIL_PATTERN.matcher(email).matches()) {
			throw new IllegalArgumentException("Enter a valid email address.");
		}

		if (!USERNAME_PATTERN.matcher(username).matches()) {
			throw new IllegalArgumentException(
					"Username must be 3 to 100 characters: letters, digits, dot, underscore or hyphen.");
		}

		if (password == null || password.length() < 8 || password.length() > 72) {
			throw new IllegalArgumentException("Password must be 8 to 72 characters.");
		}

		if (userDao.existsByUsername(username)) {
			throw new DuplicateAccountException("This username is already taken.");
		}

		if (userDao.existsByEmail(email)) {
			throw new DuplicateAccountException("This email is already registered.");
		}

		var hashedPassword = hashPassword(password);

		var normalizedDto = new SignUpRequestDto(firstName, lastName, email, username, null);

		userDao.save(normalizedDto, hashedPassword);
	}

	@Override
	public UserResponseDto login(SignInRequestDto signInRequestDto) {
		if (signInRequestDto == null || signInRequestDto.username() == null || signInRequestDto.password() == null) {
			return null;
		}

		var credentialDto = userDao.findByUsername(normalize(signInRequestDto.username()));

		if (credentialDto == null) {
			return null;
		}

		var now = LocalDateTime.now(ZoneOffset.UTC);
		var isLocked = credentialDto.lockedUntil() != null && credentialDto.lockedUntil().isAfter(now);

		if (!credentialDto.userActive() || !credentialDto.enabled() || isLocked) {
			return null;
		}

		char[] inputPassword = signInRequestDto.password().toCharArray();
		char[] hashedPassword = credentialDto.passwordHash().toCharArray();

		boolean isMatching = BCrypt.verifyer().verify(inputPassword, hashedPassword).verified;

		if (!isMatching) {
			userDao.recordFailedLogin(credentialDto.credentialId(), MAX_FAILED_ATTEMPTS, now.plusMinutes(LOCK_MINUTES));

			return null;
		}

		userDao.recordSuccessfulLogin(credentialDto.credentialId());

		return credentialDto.user();
	}

	@Override
	public String hashPassword(String password) {
		return BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray());
	}

	private String trim(String value) {
		return value == null ? "" : value.trim();
	}

	private String normalize(String value) {
		return trim(value).toLowerCase(Locale.ROOT);
	}
}
