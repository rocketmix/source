package com.essec.microservices;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class RouterConfiguration {

	@Bean
	public FallbackProvider getCircuitBreakerFallbackProvider() {
		return new CircuitBreakerDefaultFallbackProvider();
	}

	/**
	 * Needed for injection with @Value annotation
	 * 
	 * @return c
	 */
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
		c.setIgnoreUnresolvablePlaceholders(true);
		return c;
	}


}
