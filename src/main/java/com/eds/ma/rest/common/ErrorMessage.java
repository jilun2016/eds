package com.eds.ma.rest.common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ErrorMessage {
	
	private String errorCode;
	
	private String errorMessage;
	
	private String resource;
	
	private String requestId;
	/**
	 * 异常信息
	 */
	@JsonIgnore
	private final Throwable throwable;
	
	public ErrorMessage() {
		this.throwable = null;
	}

	public ErrorMessage(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.throwable = null;
	}

	public ErrorMessage(String errorCode, String errorMessage, Throwable throwable) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
		this.throwable = throwable;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
	public Throwable getThrowable() {
		return throwable;
	}


	public Response buildResponse() {
		return this.buildResponse(Status.BAD_REQUEST);
	}

	public Response buildUnauthorizedResponse() {
		return this.buildResponse(Status.UNAUTHORIZED);
	}
	
	public Response buildResponse(Status httpStatus) {
		
		final Response.ResponseBuilder response = Response.status(httpStatus);
		response.entity(this);
		response.type(MediaType.APPLICATION_JSON);
		return response.build();
	}
}
