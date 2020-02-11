package com.essec.microservices.admin.extension.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.essec.microservices.RouterApplication;
import com.essec.microservices.admin.extension.service.ApiCallSearchService;
import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class ApiCallResponseFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(RouterApplication.class);
	
	@Autowired
	private ApiCallSearchService service;	

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
			Long id = (Long) ctx.get("id");
			if (id != null) {
				this.service.update(id, responseData, ctx.getResponseStatusCode());
			}
			String line = String.format("Response, %s \r\n", responseData);
			log.debug(line);
			ctx.setResponseBody(responseData);
		} catch (IOException e) {
			log.error("Error reading body", e);
		}
		return null;
	}
}
