package com.essec.microservices;

import java.util.List;

import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.http.client.ClientHttpResponse;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

 
public class ContextAwareRibbonRoutingFilter extends RibbonRoutingFilter {
 
    private static final String ERROR_STATUS_CODE = "error.status_code";
    private static final String REQUEST_URI_KEY = "requestURI";
    private static final String SERVICE_ID_KEY = "serviceId";
 
    public ContextAwareRibbonRoutingFilter(ProxyRequestHelper helper,
			RibbonCommandFactory<?> ribbonCommandFactory,
			List<RibbonRequestCustomizer> requestCustomizers) {
 
        super(helper, ribbonCommandFactory, requestCustomizers);
    }
 
    @Override
    public Object run() {
    	RequestContext context = RequestContext.getCurrentContext();
		this.helper.addIgnoredHeaders();
		try {
			// get service ID
            String serviceId = (String) context.get(SERVICE_ID_KEY);
			RibbonCommandContext commandContext = buildCommandContext(context);
			ClientHttpResponse response = forward(commandContext);
			setResponse(response);
			return response;
		}
		catch (ZuulException ex) {
			throw new ZuulRuntimeException(ex);
		}
		catch (Exception ex) {
			throw new ZuulRuntimeException(ex);
		}
    }
}