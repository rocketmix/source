package com.essec.microservices;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.DEBUG_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

public class LogRequestFilter extends ZuulFilter {

	private static Logger log = LoggerFactory.getLogger(RouterApplication.class);
	
	@Autowired
	private AuditEventRepository repository;

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
			String principal  = "anonymous";
			Principal userPrincipal = request.getUserPrincipal();
			if (userPrincipal != null && StringUtils.isNotBlank(userPrincipal.getName())) {
				principal = userPrincipal.getName();
			}
			AuditEvent auditEvent = new AuditEvent(principal, "HTTP Request", line);
			repository.add(auditEvent);
		} catch (Exception e) {
			log.error("Error parsing request", e);
		}
		return null;
	}
}
