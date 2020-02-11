package com.essec.microservices.admin.extension.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.essec.microservices.admin.extension.repository.ApiCallRespository;

@Service
public class ApiCallThroughputService {
	
	private Map<String, Float> throughputMap = new HashMap<>();
	
	@Autowired
	private ApiCallRespository respository;
	
	public float getThroughputPerSecond() {
		float result = 0;
		for (Float aValue : this.throughputMap.values()) {
			result = result + aValue;
		}
		return result;
	}
	
	
	public float getThroughputPerSecond(String serviceId) {
		if (this.throughputMap.containsKey(serviceId)) {
			return this.throughputMap.get(serviceId);
		}
		return 0;
	}
	
	
	@Scheduled(fixedDelay = 5000)
	public void refreshStatistics() {
		
	}
	

}
