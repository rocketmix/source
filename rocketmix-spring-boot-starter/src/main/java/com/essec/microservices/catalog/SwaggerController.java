package com.essec.microservices.catalog;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ConditionalOnMissingClass("org.springframework.cloud.netflix.zuul.EnableZuulProxy.class") // Different rule on router to merge openapi.json files 
@RestController
@RequestMapping("/")
public class SwaggerController {

	@RequestMapping(value = "/swagger-ui/index.html", method = RequestMethod.GET)
	public @ResponseBody byte[] index() throws IOException {
		ClassPathResource classPathResource = new ClassPathResource("/META-INF/resources/webjars/swagger-ui/3.19.5/index.html");
		InputStream inputStream = classPathResource.getInputStream();
		String content = IOUtils.toString(inputStream);
		content = content.replaceAll("url.*:.*,", "url: \"/services/openapi.json\",");
		content = content.replace("</body>", "<script type=\"text/javascript\" src=\"/hystrix-linker.js\"></script></body>");
		return content.getBytes();
	}


}
