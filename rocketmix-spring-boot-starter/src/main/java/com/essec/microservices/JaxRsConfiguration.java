package com.essec.microservices;

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

	/**
	 * This Jackson provider allows to convert automatically JSON streams to POJO
	 * It has an @Provider annotation which make it discovered automatically by Apache CXF Spring integration
	 * which scans Spring beans thanks to cxf.jaxrs.component-scan:true parameter   
	 * 
	 * 
	 * @param module m
	 * @return p
	 */
	@Bean
	public JacksonJsonProvider jsonProvider(JsonComponentModule module) {
		JacksonJsonProvider provider = new JacksonJsonProvider();
		ObjectMapper mapper = new ObjectMapper();
		// mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.registerModule(module);
        provider.setMapper(mapper);
		return provider;
	}
	
	
	/**
	 * Allows Apache CXF and Jackson object mapper to validate JSON stream with javax validation
	 * annotations. To enable validation, don't forget to add @Valid annotation on method arguments
	 * and nested entities 
	 * 
	 * @return p
	 */
	@Bean
	public BeanValidationProvider getBeanValidationProvider() {
		return new BeanValidationProvider();
	}
		
	
	/**
	 * This input allows javax validation on method call arguments. Don't forget to add @Valid annotation
	 * on arguments you want to validate.
	 * 
	 * @return p
	 */
	@Bean
	public JAXRSBeanValidationInInterceptor getJAXRSBeanValidationInInterceptor() {
		JAXRSBeanValidationInInterceptor interceptor = new JAXRSBeanValidationInInterceptor();
		interceptor.setProvider(getBeanValidationProvider());
		return interceptor;
	}
	
	/**
	 * Allows to validate result content with javax validation. Don't forget to att @Valid annotation
	 * on method result declaration
	 * 
	 * @return p
	 */
	@Bean
	public JAXRSBeanValidationOutInterceptor getJAXRSBeanValidationOutInterceptor() {
		JAXRSBeanValidationOutInterceptor interceptor = new JAXRSBeanValidationOutInterceptor();
		interceptor.setProvider(getBeanValidationProvider());
		return interceptor;
	}
	
}
