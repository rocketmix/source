package com.essec.microservices.admin.extension.actuator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import com.essec.microservices.admin.extension.model.ApiCallEntry;
import com.essec.microservices.admin.extension.service.ApiCallSearchService;

@Component
@Endpoint(id = "apicalls")
public class ApiCallActuatorEndPoint {

	@Autowired
	private ApiCallSearchService service; 

	@ReadOperation
	public List<ApiCallEntry> findAll() {
		return this.service.findAll();
	}
	
	
	@ReadOperation
	public List<ApiCallEntry> find(@Selector String keyword) {
		return this.service.performSearch(keyword);
	}



}
