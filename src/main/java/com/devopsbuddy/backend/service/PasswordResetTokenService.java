package com.devopsbuddy.backend.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;

@Service
@Transactional(readOnly = true)
public class PasswordResetTokenService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Value("${token.expiration.length.minutes}")
	private int tokenExpirationMinutes;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PasswordResetTokenService.class);

	public PasswordResetToken findByToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}

	@Transactional
	public PasswordResetToken createPasswordResetTokenForEmail(String email) {

		PasswordResetToken passwordResetToken = null;

		User user = userRepository.findByEmail(email);

		if (null != user) {
			String token = UUID.randomUUID().toString();
			LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
			passwordResetToken = new PasswordResetToken(token, user, now, tokenExpirationMinutes);
			passwordResetTokenRepository.save(passwordResetToken);
			LOG.debug("TOKEN CREADO EXITOSAMENTE {} para el usuario {}", token, user.getUsername());
		} else {
			LOG.warn("no pudimos encontrar al usuario con email {}", email);
		}

		return passwordResetToken;
	}

}
