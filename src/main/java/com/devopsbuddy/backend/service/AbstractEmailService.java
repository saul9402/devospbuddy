package com.devopsbuddy.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import com.devopsbuddy.web.domain.frontend.FeedBackPojo;

public abstract class AbstractEmailService implements EmailService {

	@Value(value = "${default.to.address}")
	private String defaultToAddress;

	protected SimpleMailMessage prepareSimpleMailMessageFromFeedbackPojo(FeedBackPojo feedBackPojo) {
		SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
		simpleMailMessage.setTo(defaultToAddress);
		simpleMailMessage.setFrom(feedBackPojo.getEmail());
		simpleMailMessage.setSubject("DevOpsBuddy FeedBack received from " + feedBackPojo.getFirstName() + " "
				+ feedBackPojo.getLastName() + "!");
		simpleMailMessage.setText(feedBackPojo.getFeedback());
		return simpleMailMessage;
	}

	@Override
	public void sendFeedbackEmail(FeedBackPojo feedBackPojo) {
		sendGenericEmailMessage(prepareSimpleMailMessageFromFeedbackPojo(feedBackPojo));
	}

}
