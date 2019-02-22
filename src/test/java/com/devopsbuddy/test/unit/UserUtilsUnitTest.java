package com.devopsbuddy.test.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.utils.UserUtils;
import com.devopsbuddy.web.controllers.ForgotMyPasswordController;
import com.devopsbuddy.web.domain.frontend.BasicAccountPayload;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

public class UserUtilsUnitTest {

	private MockHttpServletRequest mockHttpServletRequest;
	
	//sirve para llenar POJO's con datos aleatorios, :')
	private PodamFactory podamFactory; 

	@Before
	public void init() {
		// crea una instancia para simular un contenedor de aplicaciones como TOMCAT
		mockHttpServletRequest = new MockHttpServletRequest();
		podamFactory = new PodamFactoryImpl();
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
	
	   @Test
	    public void mapWebUserToDomainUser() {

	        BasicAccountPayload webUser = podamFactory.manufacturePojoWithFullData(BasicAccountPayload.class);
	        webUser.setEmail("me@example.com");
	        User user = UserUtils.fromWebUserToDomainUser(webUser);
	        assertNotNull(user);
	        assertEquals(webUser.getUsername(), user.getUsername());
	        assertEquals(webUser.getPassword(), user.getPassword());
	        assertEquals(webUser.getFirstName(), user.getFirstName());
	        assertEquals(webUser.getLastName(), user.getLastName());
	        assertEquals(webUser.getEmail(), user.getEmail());
	        assertEquals(webUser.getPhoneNumber(), user.getPhoneNumber());
	        assertEquals(webUser.getCountry(), user.getCountry());
	        assertEquals(webUser.getDescription(), user.getDescription());
	}

}
