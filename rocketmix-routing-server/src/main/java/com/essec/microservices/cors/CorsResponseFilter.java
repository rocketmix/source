package com.essec.microservices.cors;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsResponseFilter implements Filter  {

	@Value("${zuul.cors.allowed-origins:*}")
	private List<String> acceptedOrigins;
	
	private FilterConfig config;
	
    public static final String CREDENTIALS_NAME = "Access-Control-Allow-Credentials";
    public static final String ORIGIN_NAME = "Access-Control-Allow-Origin";
    public static final String METHODS_NAME = "Access-Control-Allow-Methods";
    public static final String HEADERS_NAME = "Access-Control-Allow-Headers";
    public static final String MAX_AGE_NAME = "Access-Control-Max-Age";	
    
    public static final String ACCEPTED_METHODS = "POST, GET, OPTIONS, DELETE";
    public static final String MAX_AGE_VALUE = "3600";
    public static final String ALLOWED_HEADERS = "x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN";
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;
        String acceptedOrigin = getAcceptedOrigin(request);
		if (StringUtils.isNotBlank(acceptedOrigin)) {
	        response.setHeader(ORIGIN_NAME, acceptedOrigin);
	        response.setHeader(METHODS_NAME, ACCEPTED_METHODS);
	        response.setHeader(MAX_AGE_NAME, MAX_AGE_VALUE);
	        response.setHeader(CREDENTIALS_NAME, Boolean.TRUE.toString());
	        response.setHeader(HEADERS_NAME, ALLOWED_HEADERS);
		}

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(req, resp);
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        config = filterConfig;
    }
	
	
	private String getAcceptedOrigin(HttpServletRequest httpServletRequest) {
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
