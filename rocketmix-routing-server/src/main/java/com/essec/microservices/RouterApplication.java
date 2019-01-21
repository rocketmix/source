package com.essec.microservices;

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
		SpringApplicationWithCLI.run(RouterApplication.class, args);
	}
	
	@Bean
	public FallbackProvider getCircuitBreakerFallbackProvider() {
		return new CircuitBreakerDefaultFallbackProvider();
	}

	
	
}
