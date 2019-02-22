package com.devopsbuddy.backend.service;

import org.springframework.mail.SimpleMailMessage;

public class MockMailService extends AbstractEmailService {

	/** Logger de la aplicacion */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MockMailService.class);

	@Override
	public void sendGenericEmailMessage(SimpleMailMessage message) {
		LOG.debug("SIMLUANDO UN ENVIO DE MENSAJE (CORREO)");
		LOG.info(message.toString());
		LOG.debug("EMAIL ENVIADO");

	}

}
