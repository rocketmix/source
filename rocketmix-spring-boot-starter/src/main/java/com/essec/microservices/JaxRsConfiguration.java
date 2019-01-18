package com.essec.microservices;

import javax.ws.rs.container.ContainerResponseFilter;

import org.apache.cxf.jaxrs.openapi.OpenApiFeature;
import org.apache.cxf.jaxrs.swagger.ui.SwaggerUiConfig;
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

}
