package com.essec.microservices;

import org.apache.commons.cli.Option;
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

	@Bean
	public Option getCompanyNameCommandLineOption() {
		return Option.builder().longOpt("companyName").argName("text").required(false).hasArg(true).desc("Override the company name displayed on API Portal").build();
	}

	@Bean
	public Option getLogoURLCommandLineOption() {
		return Option.builder().longOpt("logoURL").argName("url").required(false).hasArg(true)
				.desc("Set a logo displayed on the API Portal instead of the rocket logo. Should be a transparent PNG. URL must be absolute. Ex : http://www.acme.com/static/logo.png").build();
	}

}
