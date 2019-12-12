package com.essec.microservices;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfiguration {
	
	@Bean
	public LogRequestFilter getLogRequestFilter() {
		return new LogRequestFilter();
	}

	@Bean
	public LogResponseFilter getLogResponseFilter() {
		return new LogResponseFilter();
	}
	
	
}
