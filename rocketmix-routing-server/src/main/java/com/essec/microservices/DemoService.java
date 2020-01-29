package com.essec.microservices;

import javax.validation.constraints.NotBlank;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Service
@Path("/")
@Scope("prototype")
@OpenAPIDefinition(info = @Info(title = "Health check services"))
public class DemoService {

	@GET
	@Path("/hello/{user}")
	@Produces({ MediaType.TEXT_PLAIN })
	@Operation(summary = "Says hello", responses = { @ApiResponse(responseCode = "200", description = "Success")})
	public Response sayHello(@Parameter(description = "User nickname who will receive this wonderfull hello") @PathParam("user") @NotBlank String user) {
		String result = "Hello " + user;
		return Response.ok(result).build();
	}

}
