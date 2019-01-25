package com.devopsbuddy.test.unit;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.devopsbuddy.utils.UserUtils;
import com.devopsbuddy.web.controllers.ForgotMyPasswordController;

public class UserUtilsUnitTest {

	private MockHttpServletRequest mockHttpServletRequest;

	@Before
	public void init() {
		// crea una instancia para simular un contenedor de aplicaciones como TOMCAT
		mockHttpServletRequest = new MockHttpServletRequest();
	}

	@Test
	public void testPasswordResetEmailUrlContruction() throws Exception {
		mockHttpServletRequest.setServerPort(8080);// deafult is 80
		String token = UUID.randomUUID().toString();

		long userId = 123456;

		String expectedUrl = "http://localhost:8080" + ForgotMyPasswordController.CHANGE_PASSWORD_PATH + "?id=" + userId
				+ "&token=" + token;
		String actualUrl = UserUtils.createPasswordResetUrl(mockHttpServletRequest, userId, token);
		assertEquals(expectedUrl, actualUrl);
	}

}
