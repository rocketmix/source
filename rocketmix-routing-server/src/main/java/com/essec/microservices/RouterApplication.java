package com.essec.microservices;

import org.apache.commons.cli.Option;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableInstaller
@SpringBootApplication
public class RouterApplication {


	
	
	public static void main(String[] args) {
		SpringApplication.run(RouterApplication.class, args);
	}
	
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
		return Option.builder().longOpt("logoURL").argName("url").required(false).hasArg(true).desc("Set a logo displayed on the API Portal instead of the rocket logo. Should be a transparent PNG. URL must be absolute. Ex : http://www.acme.com/static/logo.png").build();
	}
	
	
}
