package com.essec.microservices.admin.extension.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.essec.microservices.RouterApplication;
import com.essec.microservices.admin.extension.model.ApiCallEntry;
import com.essec.microservices.admin.extension.service.ApiCallSearchService;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class ApiCallResponseFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(RouterApplication.class);
	
	private static final String CONTENT_TYPE_HTTP_HEADER_NAME = "content-type";
	private static final List<String> ACCEPTED_CONTENT_TYPES = Arrays.asList("html", "text", "json", "xml");

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
		String responseData = "";
		boolean isAllowedContentType = isAllowedContentType(ctx);
		if (isAllowedContentType) {
			try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
				byte[] byteArray = IOUtils.toByteArray(responseDataStream);
				responseData = new String(byteArray);
				ctx.setResponseDataStream(new ByteArrayInputStream(byteArray));
			} catch (IOException e) {
				log.error("Error reading body", e);
			}
		}
		if (!isAllowedContentType) {
			responseData = "[response not read - only text contents are read]";
		}
		Long id = (Long) ctx.get("id");
		if (id != null) {
			this.service.update(id, responseData, ctx.getResponseStatusCode());
		}
		if (log.isDebugEnabled()) {
			String line = String.format("Response, %s \r\n", StringUtils.left(responseData, ApiCallEntry.MAX_RESPONSE_LENGTH));
			log.debug(line);
		}
		return null;
	}
	
	
	private boolean isAllowedContentType(RequestContext ctx) {
		List<Pair<String,String>> zuulResponseHeaders = ctx.getZuulResponseHeaders();
		for (Pair<String,String> aResponseHeader : zuulResponseHeaders) {
			String headerKey = aResponseHeader.first();
			if (!CONTENT_TYPE_HTTP_HEADER_NAME.equalsIgnoreCase(headerKey)) { // To avoid content length mismatch
				continue;
			}
			String headerValue = aResponseHeader.second();
			for (String anAcceptedContentType : ACCEPTED_CONTENT_TYPES) {
				if (headerValue.toLowerCase().contains(anAcceptedContentType)) {
					return true;
				}
			}
		}
		return false;
	}
	
}
