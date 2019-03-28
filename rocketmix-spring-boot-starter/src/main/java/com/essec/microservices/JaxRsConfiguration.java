package com.essec.microservices;

import javax.validation.ValidationException;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.jaxrs.swagger.ui.SwaggerUiConfig;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationInInterceptor;
import org.apache.cxf.jaxrs.validation.JAXRSBeanValidationOutInterceptor;
import org.apache.cxf.validation.BeanValidationProvider;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

@Configuration
public class JaxRsConfiguration {

	@Bean
	public OpenApiFeature createOpenApiFeature() {
		final OpenApiFeature openApiFeature = new OpenApiFeature();
		openApiFeature.setPrettyPrint(true);
		openApiFeature.setSwaggerUiConfig(new SwaggerUiConfig().url("/services/openapi.json"));
		return openApiFeature;
	}

	@Bean
	public JacksonJsonProvider jsonProvider(JsonComponentModule module) {
		JacksonJsonProvider provider = new JacksonJsonProvider();
		ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        provider.setMapper(mapper);
		return provider;
	}
//
//	@Bean
//	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(mapper);
//		return converter;
//	}
	
	
	@Bean
	public ContainerResponseFilter corsFilter() {
		return new CorsResponseFilter();
	}
	
	@Bean
	public BeanValidationProvider getBeanValidationProvider() {
		return new BeanValidationProvider();
	}
		
	
	@Bean
	public JAXRSBeanValidationInInterceptor getJAXRSBeanValidationInInterceptor() {
		JAXRSBeanValidationInInterceptor interceptor = new JAXRSBeanValidationInInterceptor();
		interceptor.setProvider(getBeanValidationProvider());
		return interceptor;
	}
	
	@Bean
	public JAXRSBeanValidationOutInterceptor getJAXRSBeanValidationOutInterceptor() {
		JAXRSBeanValidationOutInterceptor interceptor = new JAXRSBeanValidationOutInterceptor();
		interceptor.setProvider(getBeanValidationProvider());
		return interceptor;
	}
	
	@Bean
	public ExceptionMapper<ValidationException> getValidationExceptionMapper() {
		return new JaxRsValidationExceptionMapper();	
	}

}
