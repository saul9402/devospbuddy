package com.devopsbuddy.web.controllers;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.util.StringUtils;

import com.devopsbuddy.backend.persistence.domain.backend.PasswordResetToken;
import com.devopsbuddy.backend.persistence.domain.backend.User;
import com.devopsbuddy.backend.service.EmailService;
import com.devopsbuddy.backend.service.I18NService;
import com.devopsbuddy.backend.service.PasswordResetTokenService;
import com.devopsbuddy.backend.service.UserService;
import com.devopsbuddy.utils.UserUtils;

@Controller
public class ForgotMyPasswordController {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ForgotMyPasswordController.class);

	public static final String EMAIL_ADDRESS_VIEW_NAME = "forgotmypassword/emailForm";

	public static final String FORGOT_PASSWORD_URL_MAPPING = "/forgotmypassword";

	public static final String MAIL_SENT_KEY = "mailSent";

	public static final String CHANGE_PASSWORD_PATH = "/changeuserpassword";

	public static final String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";

	public static final String CHANGE_PASSWORD_VIEW_NAME = "forgotmypassword/changePassword";

	private static final String PASSWORD_RESET_ATRIBUTE_NAME = "passwordReset";

	private static final String MESSAGE_ATTRIBUTE_NAME = "message";

	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	@Autowired
	private I18NService i18NService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserService userService;

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

	@RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.GET)
	public String changeUserPasswordGet(@RequestParam("id") long id, @RequestParam("token") String token, Locale locale,
			ModelMap model) {

		if (StringUtils.isEmpty(token) || id == 0) {
			LOG.error("ID de usuario {} o token {} inavlido", id, token);
			model.addAttribute(PASSWORD_RESET_ATRIBUTE_NAME, "false");
			model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "ID  de usuario o Token invalidos");
			return CHANGE_PASSWORD_VIEW_NAME;
		}

		PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);

		if (null == passwordResetToken) {
			LOG.warn("No se encotró ningun token con el valor {}", token);
			model.addAttribute(PASSWORD_RESET_ATRIBUTE_NAME, "false");
			model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Token no encontrado");
			return CHANGE_PASSWORD_VIEW_NAME;
		}

		User user = passwordResetToken.getUser();
		if (user.getId() != id) {
			LOG.error("el token {} no corresponde al id de usuario {} que se paso por parametro {}", token, id);
			model.addAttribute(PASSWORD_RESET_ATRIBUTE_NAME, "false");
			model.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18NService.getMessage("resetPassword.token.invalid", locale));
			return CHANGE_PASSWORD_VIEW_NAME;
		}

		if (LocalDateTime.now(Clock.systemUTC()).isAfter(passwordResetToken.getExpiryDate())) {
			LOG.error("El token {} expiró, T_T", token);
			model.addAttribute(PASSWORD_RESET_ATRIBUTE_NAME, "false");
			model.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18NService.getMessage("resetPassword.token.expired", locale));
			return CHANGE_PASSWORD_VIEW_NAME;
		}

		model.addAttribute("principalId", user.getId());

		// auto-autenticacion antes de redirigir al usuario
		Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(auth);
		return CHANGE_PASSWORD_VIEW_NAME;
	}

	@RequestMapping(value = CHANGE_PASSWORD_PATH)
	public String changeUserPasswordPost(@RequestParam("principal_id") long userId,
			@RequestParam("password") String password, ModelMap model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (null == authentication) {
			LOG.error("Alguien si autenticacion trató de entrar al metodo");
			model.addAttribute(PASSWORD_RESET_ATRIBUTE_NAME, "false");
			model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "No estas autorizado para esto");
			return CHANGE_PASSWORD_VIEW_NAME;
		}

		User user = (User) authentication.getPrincipal();
		if (user.getId() != userId) {
			LOG.error("Un usuario diferente trató de cambiar la contraseña de usuario");
			model.addAttribute(PASSWORD_RESET_ATRIBUTE_NAME, "false");
			model.addAttribute(MESSAGE_ATTRIBUTE_NAME, "no estas autorizado para esto");
			return CHANGE_PASSWORD_VIEW_NAME;
		}

		userService.updateUserPassword(userId, password);
		LOG.info("La contraseña se cambió satisfactoriamente para el usuario {}", user.getUsername());
		model.addAttribute(PASSWORD_RESET_ATRIBUTE_NAME, "true");

		return CHANGE_PASSWORD_VIEW_NAME;
	}

}
