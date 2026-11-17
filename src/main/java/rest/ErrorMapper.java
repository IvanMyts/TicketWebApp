package rest;

import dto.response.ErrorResponse;
import exception.ConflictException;
import exception.InvalidRequestException;
import exception.ObjectNotFoundException;

import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
public class ErrorMapper implements ExceptionMapper<Exception> {
    private static final Logger LOG = Logger.getLogger(ErrorMapper.class.getName());

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
            if (cause instanceof ConstraintViolationException violation) {
                return ValidationErrorMapper.toResponse400(violation);
            }
            if (cause instanceof PersistenceException) {
                LOG.log(Level.WARNING, "Ошибка базы данных", exception);
                return response(409, "Операция нарушает ограничения данных");
            }
            if (cause instanceof WebApplicationException web) {
                int status = web.getResponse().getStatus();
                return response(status, switch (status) {
                    case 400 -> "Некорректный запрос: проверьте формат JSON и типы значений";
                    case 404 -> "Адрес не найден";
                    case 405 -> "Метод запроса не поддерживается";
                    case 415 -> "Отправьте данные в формате JSON";
                    default -> "Не удалось выполнить запрос";
                });
            }
        }
        LOG.log(Level.SEVERE, "Необработанная ошибка", exception);
        return response(500, "Внутренняя ошибка сервера");
    }

    public static Response response(int status, String message) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(message))
                .build();
    }
}
