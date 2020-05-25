package com.essec.microservices.cors;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class CorsResponseFilter extends ZuulFilter  {

	private static final String BLANK_STRING = "";

	private static final String DELIMITER = ",";

	private static final int FILTER_ORDER = 3;
	
    public static final String CREDENTIALS_NAME = "Access-Control-Allow-Credentials";
    public static final String ORIGIN_NAME = "Access-Control-Allow-Origin";
    public static final String METHODS_NAME = "Access-Control-Allow-Methods";
    public static final String HEADERS_NAME = "Access-Control-Allow-Headers";
    public static final String MAX_AGE_NAME = "Access-Control-Max-Age";	

	
	@Value("${zuul.cors.allowed-origins}")
	private List<String> acceptedOrigins;
	
	@Value("${zuul.cors.accepted-methods}")
	private List<String> acceptedMethods;
	
	@Value("${zuul.cors.allowed-headers}")
	private List<String> allowedHeaders;
	
	@Value("${zuul.cors.allow-credentials}")
	private Boolean allowCredentials;
	
	@Value("${zuul.cors.access-control-max-age}")
	private int accessControlMaxAge;
	
	
	@Override
	public String filterType() {
		return POST_TYPE;
	}

	@Override
	public int filterOrder() {
		return FILTER_ORDER;
	}
	
	@Override
	public boolean shouldFilter() {
		RequestContext context = RequestContext.getCurrentContext();
	    return context.getThrowable() == null
	           && (!context.getZuulResponseHeaders().isEmpty()
	               || context.getResponseDataStream() != null
	               || context.getResponseBody() != null);
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext requestContext = RequestContext.getCurrentContext();
		requestContext = removeExisingCORSResponseHeader(requestContext);
		requestContext = preserveOriginResponseHeaders(requestContext);
		requestContext = injectCORSResponseHeaders(requestContext);
	    return null;		
	}

	private RequestContext injectCORSResponseHeaders(RequestContext requestContext) {
		String acceptedOrigin = getAcceptedOrigin(requestContext);
		if (StringUtils.isNotBlank(acceptedOrigin)) {
			requestContext.addZuulResponseHeader(METHODS_NAME, String.join(DELIMITER, this.acceptedMethods));
			requestContext.addZuulResponseHeader(HEADERS_NAME, String.join(DELIMITER, this.allowedHeaders));
			requestContext.addZuulResponseHeader(CREDENTIALS_NAME, this.allowCredentials.toString());
			requestContext.addZuulResponseHeader(MAX_AGE_NAME, this.accessControlMaxAge + BLANK_STRING);
			requestContext.addZuulResponseHeader(ORIGIN_NAME, acceptedOrigin);
		}
		return requestContext;
	}
	
	private RequestContext removeExisingCORSResponseHeader(RequestContext requestContext) {
		List<Pair<String,String>> originResponseHeaders = requestContext.getOriginResponseHeaders();
		List<Pair<String,String>> toRemoveFromOriginHeaders = new ArrayList<>();
		for (Pair<String,String> anOriginResponseHeader : originResponseHeaders) {
			String headerKey = anOriginResponseHeader.first();
			if (CREDENTIALS_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromOriginHeaders.add(anOriginResponseHeader);
				continue;
			}
			if (ORIGIN_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromOriginHeaders.add(anOriginResponseHeader);
				continue;
			}
			if (METHODS_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromOriginHeaders.add(anOriginResponseHeader);
				continue;
			}
			if (HEADERS_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromOriginHeaders.add(anOriginResponseHeader);
				continue;
			}
			if (MAX_AGE_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromOriginHeaders.add(anOriginResponseHeader);
				continue;
			}
		}
		originResponseHeaders.removeAll(toRemoveFromOriginHeaders);
		List<Pair<String,String>> zuulResponseHeaders = requestContext.getZuulResponseHeaders();
		List<Pair<String,String>> toRemoveFromZuulHeaders = new ArrayList<>();
		for (Pair<String,String> anZuulResponseHeader : zuulResponseHeaders) {
			String headerKey = anZuulResponseHeader.first();
			if (CREDENTIALS_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromZuulHeaders.add(anZuulResponseHeader);
				continue;
			}
			if (ORIGIN_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromZuulHeaders.add(anZuulResponseHeader);
				continue;
			}
			if (METHODS_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromZuulHeaders.add(anZuulResponseHeader);
				continue;
			}
			if (HEADERS_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromZuulHeaders.add(anZuulResponseHeader);
				continue;
			}
			if (MAX_AGE_NAME.equalsIgnoreCase(headerKey)) {
				toRemoveFromZuulHeaders.add(anZuulResponseHeader);
				continue;
			}
		}
		zuulResponseHeaders.removeAll(toRemoveFromZuulHeaders);
		return requestContext;
	}

	private RequestContext preserveOriginResponseHeaders(RequestContext requestContext) {
		List<Pair<String,String>> originResponseHeaders = requestContext.getOriginResponseHeaders();
		List<Pair<String,String>> zuulResponseHeaders = requestContext.getZuulResponseHeaders();
		List<String> zuulReponseHeadersKeys = new ArrayList<>();
		if (zuulResponseHeaders != null) {
			zuulReponseHeadersKeys.addAll(zuulResponseHeaders.stream().map(p -> p.first()).collect(Collectors.toList()));
		}
		for (Pair<String,String> anOriginResponseHeader : originResponseHeaders) {
			String headerKey = anOriginResponseHeader.first();
			if ("Content-Length".equalsIgnoreCase(headerKey)) { // To avoid content length mismatch
				continue;
			}
			boolean isAlreadyContained = zuulReponseHeadersKeys.stream().anyMatch(headerKey::equalsIgnoreCase);
			if (isAlreadyContained) {
				continue;
			}
			requestContext.addZuulResponseHeader(anOriginResponseHeader.first(), anOriginResponseHeader.second());
		}
		return requestContext;
	}
	
	private String getAcceptedOrigin(RequestContext requestContext) {
		HttpServletRequest httpServletRequest = requestContext.getRequest();
		boolean isWildcardSupported = this.acceptedOrigins.contains("*");
		try {
			String origin = httpServletRequest.getHeader("origin");
			if (StringUtils.isBlank(origin)) {
				String referer = httpServletRequest.getHeader("referer");
				if (StringUtils.isBlank(referer)) {
					return "*"; // Default case for sec-fetch-mode = navigate
				}
				URI originURL = new URI(referer);
				origin = originURL.getScheme() + "://" + originURL.getAuthority();
			}
			if (StringUtils.isBlank(origin)) {
				return isWildcardSupported ? "*" : null;
			}
			for (String anAcceptedOrogin : this.acceptedOrigins) {
				if (match(anAcceptedOrogin, origin)) {
					return origin;
				}
			}
		} catch (URISyntaxException e) {
			return isWildcardSupported ? "*" : null;
		}
		return isWildcardSupported ? "*" : null;
	}


	
	// The main function that checks if  
	// two given strings match. The first string  
	// may contain wildcard characters 
	private static boolean match(String first, String second)  
	{ 
	  
	    // If we reach at the end of both strings,  
	    // we are done 
	    if (first.length() == 0 && second.length() == 0) 
	        return true; 
	  
	    // Make sure that the characters after '*'  
	    // are present in second string.  
	    // This function assumes that the first 
	    // string will not contain two consecutive '*' 
	    if (first.length() > 1 && first.charAt(0) == '*' &&  
	                              second.length() == 0) 
	        return false; 
	  
	    // If the first string contains '?',  
	    // or current characters of both strings match 
	    if ((first.length() > 1 && first.charAt(0) == '?') ||  
	        (first.length() != 0 && second.length() != 0 &&  
	         first.charAt(0) == second.charAt(0))) 
	        return match(first.substring(1),  
	                     second.substring(1)); 
	  
	    // If there is *, then there are two possibilities 
	    // a) We consider current character of second string 
	    // b) We ignore current character of second string. 
	    if (first.length() > 0 && first.charAt(0) == '*') 
	        return match(first.substring(1), second) ||  
	               match(first, second.substring(1)); 
	    return false; 
	} 

}
