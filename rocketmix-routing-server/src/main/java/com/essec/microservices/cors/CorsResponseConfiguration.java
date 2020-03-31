package com.essec.microservices.cors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CorsResponseConfiguration {

	
	@Bean
	public CorsResponseFilter getCorsResponseFilter() {
	    return new CorsResponseFilter();
	}

}
