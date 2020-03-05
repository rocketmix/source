package com.essec.microservices.admin.extension.actuator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import com.essec.microservices.admin.extension.service.ApiCallThroughputService;

@Component
@Endpoint(id = "apicalls-throughput")
public class ApiCallActuatorCounterEndPoint {

	@Autowired
	private ApiCallThroughputService throughputService;

	@ReadOperation
	public float throughput() {
		return this.throughputService.getThroughputPerSecond();
	}


}
