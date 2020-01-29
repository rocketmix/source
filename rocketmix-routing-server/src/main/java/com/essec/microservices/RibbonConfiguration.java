package com.essec.microservices;

import java.util.List;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
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

	@Bean
	public RibbonRoutingFilter ribbonRoutingFilter(ProxyRequestHelper helper,
			RibbonCommandFactory<?> ribbonCommandFactory, List<RibbonRequestCustomizer> requestCustomizers) {
		RibbonCommandFactoryDecorator ribbonCommandFactoryDecorator = new RibbonCommandFactoryDecorator(ribbonCommandFactory);
		RibbonRoutingFilter filter = new ContextAwareRibbonRoutingFilter(helper, ribbonCommandFactoryDecorator, requestCustomizers);
		return filter;
	}

}
