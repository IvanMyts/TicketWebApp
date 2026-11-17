package rest;

import dto.response.ErrorResponse;
import exception.ConflictException;
import exception.InvalidRequestException;
import exception.ObjectNotFoundException;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
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
                ConstraintViolationException violation = (ConstraintViolationException) cause;
                StringBuilder message = new StringBuilder();
                for (ConstraintViolation<?> item : violation.getConstraintViolations()) {
                    if (message.length() > 0) {
                        message.append("; ");
                    }
                    message.append(item.getMessage());
                }
                return response(400, message.toString());
            }
            if (cause instanceof PersistenceException) {
                return response(409, "Операция нарушает ограничения данных");
            }
            if (cause instanceof WebApplicationException) {
                int status = ((WebApplicationException) cause).getResponse().getStatus();
                String message = "Не удалось выполнить запрос";
                if (status == 400) {
                    message = "Некорректный запрос: проверьте формат JSON и типы значений";
                } else if (status == 404) {
                    message = "Адрес не найден";
                } else if (status == 405) {
                    message = "Метод запроса не поддерживается";
                } else if (status == 415) {
                    message = "Отправьте данные в формате JSON";
                }
                return response(status, message);
            }
        }
        return response(500, "Внутренняя ошибка сервера");
    }

    public static Response response(int status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(message))
                .build();
    }
}
