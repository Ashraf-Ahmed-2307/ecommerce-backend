package com.codegnan.app.ecommercebackend.user.dao;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.codegnan.app.ecommercebackend.user.dto.CredentialDto;
import com.codegnan.app.ecommercebackend.user.dto.SignUpRequestDto;
import com.codegnan.app.ecommercebackend.user.dto.UserResponseDto;
import com.codegnan.app.ecommercebackend.user.entity.Credential;
import com.codegnan.app.ecommercebackend.user.entity.Role;
import com.codegnan.app.ecommercebackend.user.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

@Repository
public class UserDaoImpl implements UserDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Long save(SignUpRequestDto signUpRequestDto, String passwordHash) {
		var roleJpql = "SELECT r FROM Role r WHERE r.roleName = :roleName";

		List<Role> rolesList = entityManager.createQuery(roleJpql, Role.class)
				.setParameter("roleName", "CUSTOMER")
				.getResultList();

		if (rolesList.isEmpty()) {
			throw new IllegalStateException("Role CUSTOMER is missing. Run auth_schema.sql to seed the roles.");
		}

		var now = LocalDateTime.now(ZoneOffset.UTC);

		var user = new User();
		user.setFirstName(signUpRequestDto.firstName());
		user.setLastName(signUpRequestDto.lastName());
		user.setEmail(signUpRequestDto.email());
		user.setActive(true);
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		user.getRoles().add(rolesList.get(0));
		entityManager.persist(user);

		var credential = new Credential();
		credential.setUser(user);
		credential.setUsername(signUpRequestDto.username());
		credential.setPasswordHash(passwordHash);
		credential.setEnabled(true);
		credential.setFailedLoginAttempts(0);
		credential.setPasswordChangedAt(now);
		credential.setCreatedAt(now);
		credential.setUpdatedAt(now);
		entityManager.persist(credential);

		return user.getId();
	}

	@Override
	public boolean existsByUsername(String username) {
		var jpql = "SELECT COUNT(c) FROM Credential c WHERE c.username = :username";

		Long count = entityManager.createQuery(jpql, Long.class)
				.setParameter("username", username)
				.getSingleResult();

		return count > 0;
	}

	@Override
	public boolean existsByEmail(String email) {
		var jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email";

		Long count = entityManager.createQuery(jpql, Long.class)
				.setParameter("email", email)
				.getSingleResult();

		return count > 0;
	}

	@Override
	public CredentialDto findByUsername(String username) {
		CredentialDto credentialDto = null;

		var jpql = "SELECT c FROM Credential c JOIN FETCH c.user WHERE c.username = :username";

		List<Credential> credentialsList = entityManager.createQuery(jpql, Credential.class)
				.setParameter("username", username)
				.getResultList();

		if (!credentialsList.isEmpty()) {
			var credential = credentialsList.get(0);
			var user = credential.getUser();

			var userResponseDto = new UserResponseDto(
					user.getId(),
					user.getFirstName(),
					user.getLastName(),
					user.getEmail());

			credentialDto = new CredentialDto(
					credential.getId(),
					credential.getUsername(),
					credential.getPasswordHash(),
					credential.isEnabled(),
					credential.getFailedLoginAttempts(),
					credential.getLockedUntil(),
					userResponseDto,
					user.isActive());
		}

		return credentialDto;
	}

	@Override
	public boolean recordFailedLogin(long credentialId, int maxAttempts, LocalDateTime lockedUntil) {
		var credential = entityManager.find(Credential.class, credentialId, LockModeType.PESSIMISTIC_WRITE);

		if (credential == null) {
			return false;
		}

		int attempts = credential.getFailedLoginAttempts() + 1;

		if (attempts >= maxAttempts) {
			credential.setLockedUntil(lockedUntil);
			attempts = 0;
		}

		credential.setFailedLoginAttempts(attempts);
		credential.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

		return true;
	}

	@Override
	public boolean recordSuccessfulLogin(long credentialId) {
		var credential = entityManager.find(Credential.class, credentialId, LockModeType.PESSIMISTIC_WRITE);

		if (credential == null) {
			return false;
		}

		var now = LocalDateTime.now(ZoneOffset.UTC);

		credential.setFailedLoginAttempts(0);
		credential.setLockedUntil(null);
		credential.setLastLoginAt(now);
		credential.setUpdatedAt(now);

		return true;
	}
}
