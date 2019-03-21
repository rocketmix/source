package com.essec.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@EnableEurekaServer
@EnableAdminServer
@EnableDiscoveryClient
@EnableInstaller
@SpringBootApplication
public class ManagementServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ManagementServerApplication.class, args);
	}


	
}
