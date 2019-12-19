package com.essec.microservices.actuator.apicalls;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ApiCallConfiguration {
	
	@Bean
	public ApiCallRequestFilter getLogRequestFilter() {
		return new ApiCallRequestFilter();
	}

	@Bean
	public ApiCallResponseFilter getLogResponseFilter() {
		return new ApiCallResponseFilter();
	}


	
	
	
	
}
