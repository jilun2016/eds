package com.eds.ma.rest.integration;


import com.eds.ma.exception.BizCoreRuntimeException;
import com.eds.ma.rest.common.ErrorMessage;
import com.eds.ma.util.ErrorCodeMessageUtil;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class BizExceptionMapper implements ExceptionMapper<BizCoreRuntimeException> {


    @Override
    public Response toResponse(BizCoreRuntimeException exception) {

        String message = null;
        String errorCode = exception.getErrorCode();
        if (exception.getErrorCode() != null) {
            message = ErrorCodeMessageUtil.getErrorCodeMessage(exception.getErrorCode(),
                    exception.getErrorContents());
        } else if (exception.getMessage() != null) {
            message = exception.getMessage();
        }

        ErrorMessage errorMessage = new ErrorMessage(errorCode, message);

        return errorMessage.buildResponse();
    }

}
