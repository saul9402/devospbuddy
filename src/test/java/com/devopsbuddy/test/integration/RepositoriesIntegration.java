package com.devopsbuddy.test.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.Role;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.backend.persistence.repositories.PlanRepository;
import com.devopsbuddy.backend.persistence.repositories.RoleRepository;
import com.devopsbuddy.backend.persistence.repositories.UserRepository;
import com.devopsbuddy.enums.PlansEnum;
import com.devopsbuddy.enums.RolesEnum;
import com.devopsbuddy.utils.UserUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class RepositoriesIntegration {

	@Autowired
	private PlanRepository planRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private UserRepository userRepository;

//	private static final int BASIC_PLAN_ID = 1;

//	private static final int BASIC_ROLE_ID = 1;

	// se ejecuta antes de los test pata verificar que los repositorios no son nulos
	@Before
	public void init() {
		assertNotNull(planRepository);
		assertNotNull(roleRepository);
		assertNotNull(userRepository);
	}

	@Test
	public void testCreateNewPlan() throws Exception {
		Plan basicPlan = createPlan(PlansEnum.BASIC);
		planRepository.save(basicPlan);
		Plan retrievedPlan = planRepository.findOne(PlansEnum.BASIC.getId());
		assertNotNull(retrievedPlan);
	}

	private Plan createPlan(PlansEnum plansEnum) {
		return new Plan(plansEnum);
//		Plan plan = new Plan();
//		plan.setId(BASIC_PLAN_ID);
//		plan.setName("BASIC");
//		return plan;
	}

	@Test
	public void createNewUser() throws Exception {
//		Plan basicPlan = createPlan(PlansEnum.BASIC);
//		planRepository.save(basicPlan);
//		User basicUser = UserUtils.createBasicUser();
//		basicUser.setPlan(basicPlan);
//		Role basicRole = createRole(RolesEnum.BASIC);
//		Set<UserRole> userRoles = new HashSet<>();
//		UserRole userRole = new UserRole(basicUser, basicRole);
////		userRole.setUser(basicUser);
////		userRole.setRole(basicRole);
//		userRoles.add(userRole);
//		basicUser.getUserRoles().addAll(userRoles);
//		for (UserRole ur : userRoles) {
//			roleRepository.save(ur.getRole());
//		}
//		basicUser = userRepository.save(basicUser);
		User basicUser = createUser();
		User newlyCreatedUser = userRepository.findOne(basicUser.getId());
		assertNotNull(newlyCreatedUser);
		assertTrue(newlyCreatedUser.getId() != 0);
		assertNotNull(newlyCreatedUser.getPlan());
		assertNotNull(newlyCreatedUser.getPlan().getId());
		Set<UserRole> newlyCreatedUserUserRoles = newlyCreatedUser.getUserRoles();
		for (UserRole ur : newlyCreatedUserUserRoles) {
			assertNotNull(ur.getRole());
			assertNotNull(ur.getRole().getId());
		}
	}

	@Test
	public void testDeleteUser() throws Exception {
		User basicUser = createUser();
		userRepository.delete(basicUser.getId());
	}

	private User createUser() {
		Plan basicPlan = createPlan(PlansEnum.BASIC);
		planRepository.save(basicPlan);

		User basicUser = UserUtils.createBasicUser();
		basicUser.setPlan(basicPlan);

		Role basicRole = createRole(RolesEnum.BASIC);
		roleRepository.save(basicRole);

		Set<UserRole> userRoles = new HashSet<>();
		UserRole userRole = new UserRole(basicUser, basicRole);
		userRoles.add(userRole);

		basicUser.getUserRoles().addAll(userRoles);
		basicUser = userRepository.save(basicUser);
		return basicUser;
	}

	private Role createRole(RolesEnum rolesEnum) {
		return new Role(rolesEnum);
//		Role role = new Role();
//		role.setId(BASIC_ROLE_ID);
//		role.setName("ROLE_USER");
//		return role;
	}

//	private User createBasicUser() {
//		User user = new User();
//		user.setUsername("basicUser");
//		user.setPassword("secret");
//		user.setEmail("me@example.com");
//		user.setFirstName("firstName");
//		user.setLastName("lastName");
//		user.setPhoneNumber("123456789123");
//		user.setCountry("GB");
//		user.setEnabled(true);
//		user.setDescription("A basic user");
//		user.setProfileImageUrl("https://blabla.images.com/basicuser");
//		return user;
//	}

}
