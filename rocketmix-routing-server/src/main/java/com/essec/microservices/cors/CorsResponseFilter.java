package com.essec.microservices.cors;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class CorsResponseFilter extends ZuulFilter  {

	private static final int FILTER_ORDER = 3;
	
	@Value("${zuul.cors.allowed-origins:*}")
	private List<String> acceptedOrigins;
	
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
		String acceptedOrigin = getAcceptedOrigin(requestContext);
		if (StringUtils.isBlank(acceptedOrigin)) {
			return null;
		}
		List<Pair<String, String>> filteredResponseHeaders = new ArrayList<>();
	    List<Pair<String, String>> zuulResponseHeaders = requestContext.getZuulResponseHeaders();
	    if (zuulResponseHeaders != null) {
	    	filteredResponseHeaders.add(new Pair<>("Access-Control-Allow-Credentials", "true"));
	    	filteredResponseHeaders.add(new Pair<>("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT"));
	    	filteredResponseHeaders.add(new Pair<>("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-Codingpedia"));
	    	filteredResponseHeaders.add(new Pair<>("Access-Control-Allow-Origin", acceptedOrigin));
	    }
	    requestContext.put("zuulResponseHeaders", filteredResponseHeaders);
	    return null;		
	}
	
	private String getAcceptedOrigin(RequestContext requestContext) {
		HttpServletRequest httpServletRequest = requestContext.getRequest();
		boolean isWildcardSupported = this.acceptedOrigins.contains("*");
		try {
			String origin = httpServletRequest.getHeader("origin");
			if (StringUtils.isBlank(origin)) {
				String referer = httpServletRequest.getHeader("referer");
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
