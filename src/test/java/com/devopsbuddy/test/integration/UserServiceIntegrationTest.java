package com.devopsbuddy.test.integration;

import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devopsbuddy.backend.persistence.domain.backend.User;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class UserServiceIntegrationTest extends AbstractServiceIntegrationTest {

	@Rule
	public TestName testName = new TestName();

	@Test
	public void testCreateNewUser() throws Exception {

		User user = createUser(testName);
		assertNotNull(user);
		assertNotNull(user.getId());

	}

}
