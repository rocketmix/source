package com.essec.microservices.admin.extension.actuator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.essec.microservices.admin.extension.model.ApiCallEntry;
import com.essec.microservices.admin.extension.service.ApiCallSearchService;
import com.essec.microservices.admin.extension.service.ApiCallThroughputService;

@Component
@RestControllerEndpoint(id = "apicalls")
public class ApiCallActuatorEndpoint {

	@Autowired
	private ApiCallThroughputService throughputService;

	@Autowired
	private ApiCallSearchService searchService;

	@GetMapping(path = "", produces = {MediaType.APPLICATION_JSON_VALUE})
	public List<String> listEndpoints() {
		List<String> result = new ArrayList<>();
		for (Method aMethod : this.getClass().getMethods()) {
			if (!Modifier.isPublic(aMethod.getModifiers())) {
				continue;
			}
			if (aMethod.isAnnotationPresent(GetMapping.class)) {
				GetMapping annotation = aMethod.getAnnotation(GetMapping.class);
				for (String aPath : annotation.path()) {
					result.add(aPath);
				}
			}
		}
		return result;
	}

	@GetMapping(path = "/throughput", produces = { MediaType.APPLICATION_JSON_VALUE })
	public float throughput() {
		return this.throughputService.getThroughputPerSecond();
	}

	@GetMapping(path = "/findall", produces = { MediaType.APPLICATION_JSON_VALUE })
	public List<ApiCallEntry> findAll() {
		return this.searchService.findAll();
	}

	@GetMapping(path = "/find/{keyword}", produces = { MediaType.APPLICATION_JSON_VALUE })
	public List<ApiCallEntry> find(@PathVariable("keyword") String keyword) {
		return this.searchService.performSearch(keyword);
	}

	

}
