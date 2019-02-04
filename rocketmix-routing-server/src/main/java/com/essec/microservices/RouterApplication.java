package com.essec.microservices;

import org.apache.commons.cli.Options;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
@EnableHystrixDashboard
@SpringBootApplication
public class RouterApplication {


	
	
	public static void main(String[] args) {
		InstallableSpringApplication.run(RouterApplication.class, getOptions(), args);
	}
	
	@Bean
	public FallbackProvider getCircuitBreakerFallbackProvider() {
		return new CircuitBreakerDefaultFallbackProvider();
	}
	
	
	public static Options getOptions() {
		Options options = new Options();
		options.addOption(null, "companyName", true, "Override de company name display on the API Portal");
		options.addOption(null, "logoURL", true, "Set a logo displayed on the API Portal instead of the rocket logo. Should be a transparent PNG. URL must be absolute. Ex : http://www.acme.com/static/logo.png");
		return options;
	}
	
	
	
	

	
	
}
