package com.essec.microservices;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.DEBUG_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class LogRequestFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(RouterApplication.class);

	@Override
	public String filterType() {
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return DEBUG_FILTER_ORDER;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = new HttpServletRequestWrapper(ctx.getRequest());
		String requestData = null;
		try {
			if (request.getContentLength() > 0) {
				requestData = CharStreams.toString(request.getReader());
				String line = String.format("Request,%s,%s,%s \r\n", request.getRequestURL(), request.getMethod(),
						requestData);
				log.debug(line);
			}
		} catch (Exception e) {
			log.error("Error parsing request", e);
		}
		return null;
	}
}
