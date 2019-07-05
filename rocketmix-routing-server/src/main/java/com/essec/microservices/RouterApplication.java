package com.essec.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
@EnableHystrixDashboard
@SpringBootApplication
public class RouterApplication {


	
	
	public static void main(String[] args) {
		SpringApplication.run(RouterApplication.class, args);
	}
	

	
}
