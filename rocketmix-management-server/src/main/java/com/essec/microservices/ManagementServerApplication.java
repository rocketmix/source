package com.essec.microservices;

import org.apache.commons.cli.Options;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import de.codecentric.boot.admin.server.config.EnableAdminServer;

@EnableEurekaServer
@EnableAdminServer
@EnableDiscoveryClient
@SpringBootApplication
public class ManagementServerApplication {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption(null, "port", true, "Change HTTP port (default: 8761)");		
		options.addOption(null, "managemenServerURL", false, "Unused option here. Please ignore it!");
		InstallableSpringApplication.run(ManagementServerApplication.class, options, args);
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
