package com.devopsbuddy.backend.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;

@Service
//se utiliza como buena practica para mejorar el performance de la aplicacion y evitar logs de más
@Transactional(readOnly = true)
public class UserService {

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PlanRepository planRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserService.class);

	// por defecto activa lectura y escritura de este metodo
	@Transactional
	public User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles) {

		User localUser = userRepository.findByEmail(user.getEmail());

		if (localUser != null) {
			LOG.info("User with username {} and email {} already exist. Nothing will be done. ", user.getUsername(),
					user.getEmail());
		} else {

			String encryptedPassword = passwordEncoder.encode(user.getPassword());
			user.setPassword(encryptedPassword);

			Plan plan = new Plan(plansEnum);
			// It makes sure the plans exist in the database
			if (!planRepository.exists(plansEnum.getId())) {
				plan = planRepository.save(plan);
			}

			user.setPlan(plan);

			for (UserRole ur : userRoles) {
				roleRepository.save(ur.getRole());
			}

			user.getUserRoles().addAll(userRoles);

			localUser = userRepository.save(user);
		}
		return localUser;

	}

	@Transactional
	public void updateUserPassword(long userId, String password) {
		password = passwordEncoder.encode(password);
		userRepository.updateUserPassword(userId, password);
		LOG.debug("La contraseña a sido cambiada exitosamene para el usuario con id {}", userId);

		Set<PasswordResetToken> resetTokens = passwordResetTokenRepository.findAllByUserId(userId);
		if (!resetTokens.isEmpty()) {
			passwordResetTokenRepository.delete(resetTokens);
		}
	}

	public User findByUserName(String username) {
		return userRepository.findByUsername(username);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

}
