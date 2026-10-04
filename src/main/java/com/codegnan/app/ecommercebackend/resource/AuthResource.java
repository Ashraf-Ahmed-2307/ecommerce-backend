package com.codegnan.app.ecommercebackend.resource;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codegnan.app.ecommercebackend.user.dto.SignInRequestDto;
import com.codegnan.app.ecommercebackend.user.dto.SignUpRequestDto;
import com.codegnan.app.ecommercebackend.user.dto.UserResponseDto;
import com.codegnan.app.ecommercebackend.user.service.DuplicateAccountException;
import com.codegnan.app.ecommercebackend.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@CrossOrigin(origins = { "http://localhost:5500", "http://127.0.0.1:5500" }, allowCredentials = "true")
@RestController
@RequestMapping("/rest/api/v1/auth")
public class AuthResource {
	private UserService userService;

	public AuthResource(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<String> signUpOperation(@ModelAttribute SignUpRequestDto signUpRequestDto) {
		try {
			userService.register(signUpRequestDto);

			return ResponseEntity.status(HttpStatus.CREATED).body("success");
		} catch (DuplicateAccountException duplicateAccountException) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(duplicateAccountException.getMessage());
		} catch (DataIntegrityViolationException dataIntegrityViolationException) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("The username or email is already registered.");
		} catch (IllegalArgumentException illegalArgumentException) {
			return ResponseEntity.badRequest().body(illegalArgumentException.getMessage());
		}
	}

	@PostMapping("/login")
	public ResponseEntity<UserResponseDto> signInOperation(
			@ModelAttribute SignInRequestDto signInRequestDto,
			HttpServletRequest request) {
		var userResponseDto = userService.login(signInRequestDto);

		if (userResponseDto == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		var session = request.getSession();
		request.changeSessionId();
		session.setAttribute("USERDTO", userResponseDto);

		return ResponseEntity.ok(userResponseDto);
	}
}
