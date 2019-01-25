package com.devopsbuddy.test.integration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.Plan;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.domain.backend.UserRole;
import com.devopsbuddy.enums.PlansEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class UserIntegrationTest extends AbstractInegrationTest {

	@Rule
	public TestName testName = new TestName();

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

	@Test
	public void createUser() throws Exception {
		String username = testName.getMethodName();
		String email = testName.getMethodName() + "@devospbuddy.com";
		User basicUser = createUser(username, email);
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
		String username = testName.getMethodName();
		String email = testName.getMethodName() + "@devospbuddy.com";
		User basicUser = createUser(username, email);
		userRepository.delete(basicUser.getId());
	}

}
