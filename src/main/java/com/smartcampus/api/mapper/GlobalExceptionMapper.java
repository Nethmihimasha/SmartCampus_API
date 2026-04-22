package com.smartcampus.api.mapper;

import com.smartcampus.api.model.ErrorResponse;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof WebApplicationException) {
            throw (WebApplicationException) exception;
        }

        LOGGER.log(Level.SEVERE, "Unhandled server exception", exception);
        ErrorResponse payload = new ErrorResponse(500, "INTERNAL_SERVER_ERROR", "An unexpected server error occurred");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .entity(payload)
                .build();
    }
}
