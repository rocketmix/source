package com.essec.microservices.actuator.apicalls;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.essec.microservices.RouterApplication;
import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class ApiCallResponseFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(RouterApplication.class);

	@Override
	public String filterType() {
		return POST_TYPE;
	}

	@Override
	public int filterOrder() {
		return SEND_RESPONSE_FILTER_ORDER;
	}

	@Override
	public boolean shouldFilter() {
		return true;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
			final String responseData = CharStreams.toString(new InputStreamReader(responseDataStream, "UTF-8"));
			String line = String.format("Response, %s \r\n", responseData);
			log.debug(line);
			ctx.setResponseBody(responseData);
		} catch (IOException e) {
			log.error("Error reading body", e);
		}
		return null;
	}
}
