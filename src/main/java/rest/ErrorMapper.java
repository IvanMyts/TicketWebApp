package rest;

import dto.response.ErrorResponse;
import exception.ConflictException;
import exception.InvalidRequestException;
import exception.ObjectNotFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ObjectNotFoundException) {
                return response(404, cause.getMessage());
            }
            if (cause instanceof ConflictException) {
                return response(409, cause.getMessage());
            }
            if (cause instanceof InvalidRequestException) {
                return response(400, cause.getMessage());
            }
            if (cause instanceof ConstraintViolationException) {
                StringBuilder message = new StringBuilder();
                for (ConstraintViolation<?> item : ((ConstraintViolationException) cause).getConstraintViolations()) {
                    if (message.length() > 0) {
                        message.append("; ");
                    }
                    message.append(item.getMessage());
                }
                return response(400, message.toString());
            }
        }
        return response(400, "Некорректный запрос");
    }

    private static Response response(int status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(message))
                .build();
    }
}
