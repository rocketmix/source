package com.essec.microservices;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
	
	private static final String COMPANY_NAME_TEMPLATE_KEY = "Welcome to";
	private static final String COMPANY_LOGO_TEMPLATE_KEY = "img/rocket-logo-min.png";
	
	@Value("${companyName}")
	private String companyName;
	
	@Value("${logoURL}")
	private String companyLogo;
	
	@Autowired
	private ResourceLoader resourceLoader;	
	

	@RequestMapping(value = "")
	public @ResponseBody byte[] index() throws IOException {
		ClassPathResource htmlResource = new ClassPathResource("/static/index.html");
		InputStream htmlStream = htmlResource.getInputStream();
		String content = IOUtils.toString(htmlStream);
		if (StringUtils.isNotBlank(this.companyName)) {
			content = content.replace(COMPANY_NAME_TEMPLATE_KEY, this.companyName);
		}
		if (StringUtils.isNotBlank(this.companyLogo)) {
			content = content.replace(COMPANY_LOGO_TEMPLATE_KEY, "/img/companylogo");
		}
		return content.getBytes();
	}
	
	
	@RequestMapping(value = "/img/companylogo", method = {RequestMethod.GET})
	public Resource logo() throws IOException {
		if (StringUtils.isNotBlank(this.companyLogo)) {
			Resource customLogoResource = resourceLoader.getResource(this.companyLogo);
			return customLogoResource;
		}
		ClassPathResource defaultLogoResource = new ClassPathResource("/static/" + COMPANY_LOGO_TEMPLATE_KEY);
		return defaultLogoResource;
	}
}
