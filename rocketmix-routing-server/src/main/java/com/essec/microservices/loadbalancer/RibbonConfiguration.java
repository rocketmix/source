package com.essec.microservices.loadbalancer;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IRule;

@Configuration
@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class RibbonConfiguration {

	@Bean
	public IRule ribbonRule() {
		return new AvailabilityFilteringRule();
	}

}
