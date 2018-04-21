package com.eds.ma.rest.integration;

import com.eds.ma.rest.common.ErrorMessage;
import com.eds.ma.rest.common.RestErrorCode;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;


public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

	
	@Override
	public Response toResponse(NotFoundException exception) {
		
		String message = exception.getMessage();
		if(StringUtils.isBlank(message)) message = RestErrorCode.NOT_FOUND.reason();
		
		ErrorMessage errorMessage = new ErrorMessage(RestErrorCode.NOT_FOUND.code()
			       , message
			      );
		
		return errorMessage.buildResponse(Response.Status.NOT_FOUND);
	}

}
