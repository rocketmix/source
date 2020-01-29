package com.essec.microservices.actuator.apicalls;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/dashboard")
public class ApiCallDashboardController {
	
	
	private static final String DEFAULT_HOST = "http://localhost:8080";
	

	@RequestMapping(value = {""})
	public @ResponseBody byte[] index() throws IOException {
		ClassPathResource htmlResource = new ClassPathResource("/static/dashboard.html");
		InputStream htmlStream = htmlResource.getInputStream();
		String content = IOUtils.toString(htmlStream);
		String currentServerURL = getCurrentRequest().getRequestURL().toString();
		String servletPath = getCurrentRequest().getServletPath();
		String rootUrl = currentServerURL.replace(servletPath, "");
		content = content.replace(DEFAULT_HOST, rootUrl);
		return content.getBytes();
	}
	
	private static HttpServletRequest getCurrentRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
		Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
		HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
		Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
		return servletRequest;
	}

}
