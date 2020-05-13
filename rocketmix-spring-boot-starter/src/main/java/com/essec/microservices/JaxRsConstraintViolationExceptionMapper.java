package com.essec.microservices;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

/**
 * Allow to show JSR 303 javax validation error messages if Jackson bean mapper received on unvalid JSON stream 
 * 
 * @author depellegrin
 *
 */
@Provider
@Component
public class JaxRsConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	@Override
	public Response toResponse(final ConstraintViolationException exception) {
		return Response.status(Response.Status.BAD_REQUEST).entity(prepareMessage(exception)).type("text/plain").build();
	}

	private String prepareMessage(ConstraintViolationException exception) {
		List<String> errors = new ArrayList<>();
		Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
		for (ConstraintViolation<?> v : violations) {
			errors.add(String.format("%s %s (was %s)", v.getPropertyPath(), v.getMessage(), v.getInvalidValue()));
		}
		StringBuilder msg = new StringBuilder("The request entity had the following errors:\n");
		for (String error : errors) {
			msg.append("  * ").append(error).append('\n');
		}
		return msg.toString();
	}

}
