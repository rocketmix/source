package com.essec.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.turbine.EnableTurbine;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@EnableEurekaServer
@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
@EnableTurbine
@EnableHystrixDashboard
@EnableAdminServer
@SpringBootApplication
public class RouterApplication {


	
	
	public static void main(String[] args) {
		SpringApplication.run(RouterApplication.class, args);
	}
	

	
}
