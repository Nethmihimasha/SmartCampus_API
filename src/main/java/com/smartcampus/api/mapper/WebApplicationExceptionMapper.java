package com.smartcampus.api.mapper;

import com.smartcampus.api.model.ErrorResponse;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {
    private static final Logger LOGGER = Logger.getLogger(WebApplicationExceptionMapper.class.getName());

    @Override
    public Response toResponse(WebApplicationException exception) {
        int status = exception.getResponse() == null ? 500 : exception.getResponse().getStatus();
        String message = exception.getMessage() == null || exception.getMessage().trim().isEmpty()
                ? "Request failed"
                : exception.getMessage();

        LOGGER.warning("Handled WebApplicationException with status " + status + ": " + message);

        ErrorResponse payload = new ErrorResponse(status, "HTTP_" + status, message);
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(payload)
                .build();
    }
}
