package com.essec.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

import de.codecentric.boot.admin.server.config.EnableAdminServer;


@EnableDiscoveryClient
@EnableZuulProxy
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableInstaller
@EnableEurekaServer
@EnableAdminServer
@SpringBootApplication
public class BundleServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BundleServerApplication.class, args);
	}



}
