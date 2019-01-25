package com.devopsbuddy.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.I18NService;
import com.devopsbuddy.backend.service.PasswordResetTokenService;
import com.devopsbuddy.utils.UserUtils;

@Controller
public class ForgotMyPasswordController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ForgotMyPasswordController.class);

	public static final String EMAIL_ADDRESS_VIEW_NAME = "forgotmypassword/emailForm";

	public static final String FORGOT_PASSWORD_URL_MAPPING = "/forgotmypassword";

	public static final String MAIL_SENT_KEY = "mailSent";

	public static final String CHANGE_PASSWORD_PATH = "/changeuserpassword";

	public static final String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";

	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	@Autowired
	private I18NService i18NService;

	@Autowired
	private EmailService emailService;

	@Value("${webmaster.email}")
	private String webMasterEmail;

	@RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.GET)
	public String forgotPasswordGet() {
		return EMAIL_ADDRESS_VIEW_NAME;
	}

	@RequestMapping(value = FORGOT_PASSWORD_URL_MAPPING, method = RequestMethod.POST)
	public String forgotPasswordPost(HttpServletRequest request, @RequestParam("email") String email, ModelMap model) {

		PasswordResetToken passwordResetToken = passwordResetTokenService.createPasswordResetTokenForEmail(email);

		if (null == passwordResetToken) {
			LOG.warn("No se encontró token para el email {}", email);
		} else {
			User user = passwordResetToken.getUser();
			String token = passwordResetToken.getToken();

			String resetPasswordUrl = UserUtils.createPasswordResetUrl(request, user.getId(), token);
			LOG.debug("se creo la siguiente url {}", resetPasswordUrl);

			String emailText = i18NService.getMessage(EMAIL_MESSAGE_TEXT_PROPERTY_NAME, request.getLocale());

			SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
			simpleMailMessage.setTo(user.getEmail());
			simpleMailMessage.setSubject("[Devopsbuddy]: How to reset your password");
			simpleMailMessage.setText(emailText + "\r\n" + resetPasswordUrl);
			simpleMailMessage.setFrom(webMasterEmail);

			emailService.sendGenericEmailMessage(simpleMailMessage);

		}

		model.addAttribute(MAIL_SENT_KEY, true);

		return EMAIL_ADDRESS_VIEW_NAME;
	}
}
