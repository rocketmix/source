package com.essec.microservices.admin.extension.filter;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.DEBUG_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

import java.security.Principal;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.essec.microservices.RouterApplication;
import com.essec.microservices.admin.extension.model.ApiCallEntry;
import com.essec.microservices.admin.extension.service.ApiCallSearchService;
import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class ApiCallRequestFilter extends ZuulFilter {

	private static final String URL_SEARCHED_STRING = "/services/";

	private static Logger log = LoggerFactory.getLogger(RouterApplication.class);
	
	@Autowired
	private ApiCallSearchService service;
	
	private AtomicLong threadSafeSeq = new AtomicLong(0);

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
			requestData = "" + CharStreams.toString(request.getReader());
			String line = String.format("Request,%s,%s,%s \r\n", request.getRequestURL(), request.getMethod(),
					requestData);
			log.debug(line);
			ApiCallEntry apiCall = new ApiCallEntry();
			apiCall.setId(this.threadSafeSeq.incrementAndGet());
			String requestURI = request.getRequestURI();
			String serviceId = requestURI;
			if (serviceId != null && serviceId.contains(URL_SEARCHED_STRING)) {
				serviceId = serviceId.substring(1, serviceId.indexOf(URL_SEARCHED_STRING));
			}
			apiCall.setServiceId(serviceId);
			String queryString = request.getQueryString();
			String requestURL = requestURI;
			if (queryString != null) {
				requestURL = requestURL + "?" + queryString;
			}
			apiCall.setRequestURL(requestURL);
			apiCall.setRequestData(requestData);
			Long id = service.save(apiCall);
			ctx.put("id", id);
		} catch (Exception e) {
			log.error("Error parsing request", e);
		}
		return null;
	}
}
