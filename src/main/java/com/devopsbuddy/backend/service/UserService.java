package com.devopsbuddy.backend.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.web.controllers.ContactController;

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

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserService.class);

	// por defecto activa lectura y escritura de este metodo
	@Transactional
	public User createUser(User user, PlansEnum plansEnum, Set<UserRole> userRoles) {

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

		user = userRepository.save(user);

		return user;

	}

	@Transactional
	public void updateUserPassword(long userId, String password) {
		password = passwordEncoder.encode(password);
		userRepository.updateUserPassword(userId, password);
		LOG.debug("La contraseña a sido cambiada exitosamene para el usuario con id {}", userId);
	}

}
