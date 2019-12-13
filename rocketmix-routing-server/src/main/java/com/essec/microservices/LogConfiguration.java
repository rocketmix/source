package com.essec.microservices;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteBuilder;
import org.dizitart.no2.NitriteCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogConfiguration {
	
	@Bean
	public LogRequestFilter getLogRequestFilter() {
		return new LogRequestFilter();
	}

	@Bean
	public LogResponseFilter getLogResponseFilter() {
		return new LogResponseFilter();
	}

	@Bean
	public Nitrite getDB() {
		NitriteBuilder builder = Nitrite.builder();
		return builder.compressed().openOrCreate();
	}
	
	@Bean
	public NitriteCollection getDocumentCollection(Nitrite db) {
		return db.getCollection("logger");
	}
	
	
	
	
}
