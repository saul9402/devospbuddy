package com.devopsbuddy.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

public class SmtpService extends AbstractEmailService {

	/** Logger de la aplicacion */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SmtpService.class);

	@Autowired
	private MailSender mailSender;

	@Override
	public void sendGenericEmailMessage(SimpleMailMessage message) {
		LOG.debug("SIMLUANDO UN ENVIO DE MENSAJE SMTP");
		LOG.info("CARGA");
		mailSender.send(message);
		LOG.debug("EMAIL ENVIADO");

	}

}
