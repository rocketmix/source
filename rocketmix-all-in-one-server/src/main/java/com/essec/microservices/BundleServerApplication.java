package com.essec.microservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class BundleServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouterApplication.class, args);
		SpringApplication.run(ManagementServerApplication.class, args);
	}



}
