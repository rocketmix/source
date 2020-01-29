package com.essec.microservices.circuitbreaker;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HystrixConfiguration {

	@Bean
	public FallbackProvider getCircuitBreakerFallbackProvider() {
		return new HystrixDefaultFallbackProvider();
	}
	
	
}
