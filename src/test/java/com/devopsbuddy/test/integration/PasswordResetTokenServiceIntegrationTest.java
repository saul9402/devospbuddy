package com.devopsbuddy.test.integration;

import static org.junit.Assert.assertNotNull;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.PasswordResetTokenService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class PasswordResetTokenServiceIntegrationTest extends AbstractServiceIntegrationTest {

	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	@Rule
	public TestName testName = new TestName();

	@Test
	public void testCreateNewTokenForUserEmail() {
		User user = createUser(testName);

		PasswordResetToken passwordResetToken = passwordResetTokenService
				.createPasswordResetTokenForEmail(user.getEmail());
		assertNotNull(passwordResetToken);
		assertNotNull(passwordResetToken.getToken());
	}

	@Test
	public void testFindByToken() throws Exception{
		User user = createUser(testName);
		PasswordResetToken passwordResetToken = passwordResetTokenService
				.createPasswordResetTokenForEmail(user.getEmail());
		assertNotNull(passwordResetToken);
		assertNotNull(passwordResetToken.getToken());

		PasswordResetToken token = passwordResetTokenService.findByToken(passwordResetToken.getToken());
		assertNotNull(token);

	}
}
