package com.essec.microservices;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Service
@Path("/")
@Scope("prototype")
@OpenAPIDefinition(info = @Info(title = "Demo services"))
@SecurityScheme(name = "basicAuth", type = SecuritySchemeType.HTTP, scheme = "basic")
public class DemoService {

	@GET
	@Path("/hello/{user}")
	//@PreAuthorize("hasRole('MOBAPP')")
	@Produces({ MediaType.TEXT_PLAIN })
	@Operation(summary = "Says hello", description = "Service demo with authentication. Login is 'guest' and password is 'password'", security = { @SecurityRequirement(name = "basicAuth") }, responses = { @ApiResponse(responseCode = "200", description = "Success"), @ApiResponse(responseCode = "401", description = "Unauthorized") })
	@HystrixCommand
	public Response sayHello(@Parameter(description = "User nickname who will receive this wonderfull hello") @PathParam("user") @NotBlank String user) {
		String result = "Hello " + user;
		return Response.ok(result).build();
	}

}
