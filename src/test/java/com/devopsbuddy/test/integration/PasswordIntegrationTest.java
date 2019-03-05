package com.devopsbuddy.test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.devopsbuddy.DevopsbuddyApplication;
import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.persistence.repositories.PasswordResetTokenRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DevopsbuddyApplication.class)
public class PasswordIntegrationTest extends AbstractInegrationTest {

	@Value("${token.expiration.length.minutes}")
	private int expirationTimeInMInutes;

	@Autowired
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Rule
	public TestName testName = new TestName();

	@Before
	public void init() {
		assertFalse(expirationTimeInMInutes == 0);
	}

	@Test
	public void testTokenExpirationLength() throws Exception {
		User user = createUser(testName);
		assertNotNull(user);
		assertNotNull(user.getId());

		LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
		String token = UUID.randomUUID().toString();
		LocalDateTime expectedTime = now.plusMinutes(expirationTimeInMInutes);

		PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);

		LocalDateTime actualTime = passwordResetToken.getExpiryDate();
		assertNotNull(actualTime);
		assertEquals(expectedTime, actualTime);

	}

	@Test
	public void testFindTokenByTokenValue() {

		User user = createUser(testName);

		LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
		String token = UUID.randomUUID().toString();

		createPasswordResetToken(token, user, now);
		PasswordResetToken retrievedPasswordResetToken = passwordResetTokenRepository.findByToken(token);
		assertNotNull(retrievedPasswordResetToken);
		assertNotNull(retrievedPasswordResetToken.getId());
		assertNotNull(retrievedPasswordResetToken.getUser());

	}

	@Test
	public void testDeleteToken() throws Exception {
		User user = createUser(testName);

		LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
		String token = UUID.randomUUID().toString();

		PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
		long tokenId = passwordResetToken.getId();
		passwordResetTokenRepository.delete(tokenId);
		PasswordResetToken shouldNotExistoken = passwordResetTokenRepository.findOne(tokenId);
		assertNull(shouldNotExistoken);

	}

	@Test
	public void testCascadeDeleteFromUserEntity() throws Exception {
		User user = createUser(testName);

		LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
		String token = UUID.randomUUID().toString();

		PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
		passwordResetToken.getId();

		userRepository.delete(user.getId());

		Set<PasswordResetToken> shouldBeEmpty = passwordResetTokenRepository.findAllByUserId(user.getId());
		assertTrue(shouldBeEmpty.isEmpty());
	}

	@Test
	public void testMultipleTokenAreReturnedWhenQueringByUserId() {

		User user = createUser(testName);

		LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
		String token1 = UUID.randomUUID().toString();
		String token2 = UUID.randomUUID().toString();
		String token3 = UUID.randomUUID().toString();

		Set<PasswordResetToken> tokens = new HashSet<PasswordResetToken>();

		tokens.add(createPasswordResetToken(token1, user, now));
		tokens.add(createPasswordResetToken(token2, user, now));
		tokens.add(createPasswordResetToken(token3, user, now));

		passwordResetTokenRepository.save(tokens);

		User foundUser = userRepository.findOne(user.getId());

		Set<PasswordResetToken> actualTokens = passwordResetTokenRepository.findAllByUserId(foundUser.getId());
		assertTrue(actualTokens.size() == tokens.size());
		List<String> tokensAsList = tokens.stream().map(ptr -> ptr.getToken()).sorted().collect(Collectors.toList());
		List<String> actualTokensAsList = actualTokens.stream().map(ptr -> ptr.getToken()).sorted()
				.collect(Collectors.toList());
		assertEquals(tokensAsList, actualTokensAsList);

	}

	private PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime now) {
		PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, now, expirationTimeInMInutes);
		passwordResetTokenRepository.save(passwordResetToken);
		assertNotNull(passwordResetToken.getId());
		return passwordResetToken;
	}

}
