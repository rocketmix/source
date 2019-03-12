package com.essec.microservices;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

@Component
@ManagedResource(objectName="RocketMiX:name=Routing Statistics")
public class MetricsServiceBean {

	private Map<String, Integer> serviceCounter = new HashMap<>();
	
	public void incrementCounter(String serviceName) {
		Integer counter = this.serviceCounter.get(serviceName);
		if (counter == null) {
			this.serviceCounter.put(serviceName, 1);
			return;
		}
		this.serviceCounter.put(serviceName, counter.intValue() + 1);
	}
	
	@ManagedOperation
	public Map<String, Integer> stats() {
		return this.serviceCounter;
	}
	
}
