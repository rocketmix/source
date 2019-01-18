package com.essec.microservices;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import io.swagger.v3.oas.annotations.Operation;

@Service
@Path("/")
@Scope("prototype")
public class PingService {

	@GET
	@Path("/test/ping")
	@Produces({ MediaType.TEXT_PLAIN })
	@Operation(hidden = true)
	@HystrixCommand
	public Response ping() {
		String result = "ok";
		return Response.ok(result).build();
	}

}
