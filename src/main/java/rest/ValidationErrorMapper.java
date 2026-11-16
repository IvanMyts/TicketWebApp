package rest;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.stream.Collectors;

/**
 * Ошибки валидации → 400 с текстом всех нарушений.
 * Отдельный класс нужен потому, что у RESTEasy есть свой обработчик ошибок валидации,
 * и ErrorMapper (для Exception) ему проигрывает: выбирается обработчик для более конкретного типа.
 */
@Provider
public class ValidationErrorMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        return toResponse400(exception);
    }

    static Response toResponse400(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .distinct()
                .collect(Collectors.joining("; "));
        return ErrorMapper.response(400, message.isEmpty() ? "Проверьте значения полей" : message);
    }
}
