package com.devopsbuddy.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.devopsbuddy.web.domain.frontend.FeedBackPojo;

@Controller
public class ContactController {
	
	/**Logger de la aplicacion */
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ContactController.class);

	public static final String FEEDBACK_MODEL_KEY = "feedback";

	public static final String CONTACT_US_VIEW_NAME = "contact/contact";

	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String contactGet(ModelMap model) {
		FeedBackPojo feedBackPojo = new FeedBackPojo();
		model.addAttribute(ContactController.FEEDBACK_MODEL_KEY, feedBackPojo);
		return CONTACT_US_VIEW_NAME;
	}
	

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public String contactPost(@ModelAttribute(FEEDBACK_MODEL_KEY) FeedBackPojo feedback) {
        LOG.debug("Feedback POJO content: {}", feedback);
//        emailService.sendFeedbackEmail(feedback);
        return ContactController.CONTACT_US_VIEW_NAME;
}

}
