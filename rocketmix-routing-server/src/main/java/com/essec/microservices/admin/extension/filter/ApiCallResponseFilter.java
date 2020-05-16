package com.essec.microservices.admin.extension.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.essec.microservices.RouterApplication;
import com.essec.microservices.admin.extension.model.ApiCallEntry;
import com.essec.microservices.admin.extension.service.ApiCallSearchService;
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
		Long originContentLength = ctx.getOriginContentLength();
		String responseData = "";
		if (originContentLength != null && originContentLength <= ApiCallEntry.MAX_RESPONSE_LENGTH) {
			try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
				byte[] byteArray = IOUtils.toByteArray(responseDataStream);
				responseData = new String(byteArray);
				ctx.setResponseDataStream(new ByteArrayInputStream(byteArray));
			} catch (IOException e) {
				log.error("Error reading body", e);
			}
		}
		if (originContentLength != null && originContentLength > ApiCallEntry.MAX_RESPONSE_LENGTH) {
			responseData = "[response too long to be read]";
		}
		if (originContentLength == null) {
			responseData = "[response without content length not read]";
		}
		Long id = (Long) ctx.get("id");
		if (id != null) {
			this.service.update(id, responseData, ctx.getResponseStatusCode());
		}
		String line = String.format("Response, %s \r\n", responseData);
		log.debug(line);
		return null;
	}
}
