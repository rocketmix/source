package com.essec.microservices;

import org.springframework.context.annotation.Bean;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AvailabilityFilteringRule;
import com.netflix.loadbalancer.IRule;

public class RibbonConfiguration {
	
    @Bean
    public IRule ribbonRule(final IClientConfig config) {
        return new AvailabilityFilteringRule();
    }

}
