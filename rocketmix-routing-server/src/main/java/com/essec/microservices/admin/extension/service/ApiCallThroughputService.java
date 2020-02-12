package com.essec.microservices.admin.extension.service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.essec.microservices.admin.extension.repository.ApiCallRespository;
import com.essec.microservices.admin.extension.repository.ApiCallRespository.ApiCallServiceAndCount;

@Service
public class ApiCallThroughputService {
	
	
	private Map<String, Float> throughputMap = new ConcurrentHashMap<>();
	
	private float globalThroughput = 0; 
	
	@Autowired
	private ApiCallRespository respository;
	
	public float getThroughputPerSecond() {
		return this.globalThroughput;
	}
	
	
	public float getThroughputPerSecond(String serviceId) {
		if (this.throughputMap.containsKey(serviceId)) {
			return this.throughputMap.get(serviceId);
		}
		return 0;
	}
	
	
	@Scheduled(fixedDelay = 5000)
	public void refreshStatistics() {
		int totalCounter = 0;
		Date from = Date.from(Instant.now().minusSeconds(5));
		List<ApiCallServiceAndCount> countWithActivityDateAdter = this.respository.countWithActivityDateAdter(from);
		Map<String, Float> result = new HashMap<>();
		for (ApiCallServiceAndCount a : countWithActivityDateAdter) {
			float throughput = a.getCounter() / 5;
			String serviceId = a.getService();
			result.put(serviceId, throughput);
			totalCounter = totalCounter + a.getCounter();
		}
		this.throughputMap.clear();
		this.throughputMap.putAll(result);
		this.globalThroughput = totalCounter / 5;
	}
	

}
