package com.essec.microservices;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@Configuration
public class HystrixConfiguration {

	@Autowired
	private EurekaClient eurekaClient;
	

	@Scheduled(fixedDelay = 10000)
	public void updateHystrixCommand() {
		try {
			List<Application> applications = eurekaClient.getApplications().getRegisteredApplications();
			for (Application application : applications) {
				try {
					List<InstanceInfo> applicationsInstances = application.getInstances();
					for (InstanceInfo applicationsInstance : applicationsInstances) {
						String name = applicationsInstance.getAppName();
						System.out.println("Application name found for docs : " + name);
						break;
					}
				} catch (Exception e1) {
					e1.printStackTrace();
					continue;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
}
