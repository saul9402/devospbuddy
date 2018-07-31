package com.devopsbuddy.backend.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import com.devopsbuddy.web.domain.frontend.FeedBackPojo;

@Service
public interface EmailService {

	public void sendFeedbackEmail(FeedBackPojo feedBackPojo);

	public void sendGenericEmailMessage(SimpleMailMessage message);

}
