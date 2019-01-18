package com.essec.microservices;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/")
public class AdminController {

	/**
	 * Hack to support GET on logout button on Spring Boot Admin 
	 */
	@RequestMapping(value = "/admin/logout", method = {RequestMethod.GET, RequestMethod.POST} )
	public RedirectView logout() {
		RedirectView redirectView = new RedirectView();
	    redirectView.setUrl("/");
	    return redirectView;
	}

}
