package com.essec.microservices.actuator.apicalls;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "apicalls")
public class ApiCallActuatorEndPoint {

	@Autowired
	private ApiCallService service; 

	@ReadOperation
	public List<ApiCall> findAll() {
		return this.service.findAll();
	}
	
	
	@ReadOperation
	public List<ApiCall> find(@Selector String keyword) {
		return this.service.performSearch(keyword);
	}



}
