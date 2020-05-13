package com.essec.microservices;

import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.impl.WebApplicationExceptionMapper;
import org.springframework.stereotype.Component;

/**
 * Override default CXF WebApplicationExceptionMapper which doesn't expose error messages   
 * 
 * @author depellegrin
 *
 */
@Provider
@Component
public class JaxRsWebApplicationExceptionMapper extends WebApplicationExceptionMapper {

	public JaxRsWebApplicationExceptionMapper() {
		super();
		setAddMessageToResponse(true);
	}

}
