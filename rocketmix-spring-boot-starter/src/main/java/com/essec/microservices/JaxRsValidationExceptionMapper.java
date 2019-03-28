package com.essec.microservices;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.ValidationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.validation.ResponseConstraintViolationException;
import org.slf4j.LoggerFactory;

/**
 * Inspired from Hyun Woo Son on 1/9/18
 **/
@Provider
public class JaxRsValidationExceptionMapper implements ExceptionMapper<ValidationException> {

	private static org.slf4j.Logger logger = LoggerFactory.getLogger(JaxRsValidationExceptionMapper.class);

	public Response toResponse(ValidationException exception) {
		Response.Status errorStatus = Response.Status.INTERNAL_SERVER_ERROR;
		ExceptionResult result = new ExceptionResult();
		if (exception instanceof ConstraintViolationException) {
			ConstraintViolationException constraint = (ConstraintViolationException) exception;
			
			for (ConstraintViolation<?> violation : constraint.getConstraintViolations()) {
				logger.info("error {}", violation.getRootBeanClass().getSimpleName() + "." + violation.getPropertyPath() + ": " + violation.getMessage());
				String cause = violation.getMessage();
				Path property = violation.getPropertyPath();
				StringBuilder message = new StringBuilder();
				message.append("Property ").append(property);
				if (StringUtils.isNotBlank(cause)) {
					message.append(" with error: ").append(cause);
				}
				result.addMessage(message.toString());
			}

			if (!(constraint instanceof ResponseConstraintViolationException)) {
				errorStatus = Response.Status.BAD_REQUEST;
			}
		} else {
			result.addMessage("Unexcepted error: " + exception.getMessage());
			logger.debug("Error de validacion {}", exception.getMessage(), exception);
		}
		return Response.status(errorStatus).entity(result).build();
	}
	
	public class ExceptionResult {
		private List<String> errors = new ArrayList<>();

		
		
		public List<String> getErrors() {
			return this.errors;
		}

		public void setErrors(List<String> messages) {
			this.errors = messages;
		}
		
		public void addMessage(String message) {
			this.errors.add(message);
		}
		
		
	}

}