package com.essec.microservices.metrics;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MetricsConfiguration {

	@Bean
	public Filter getMetricsFilter(final MeterRegistry registry, final MetricsServiceBean metricsServiceBean) {
		return new Filter() {
			@Override
			public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
					throws IOException, ServletException {
				if (request instanceof HttpServletRequest) {
					final String url = ((HttpServletRequest) request).getRequestURL().toString();
					metricsServiceBean.incrementCounter(url);
					chain.doFilter(request, response);
				}
			}

			@Override
			public void init(final FilterConfig filterConfig) throws ServletException {
				// Nothing to do
			}

			@Override
			public void destroy() {
				// Nothing to do
			}
		};
	}

}
