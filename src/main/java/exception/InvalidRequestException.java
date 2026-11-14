package exception;

/**
 * Некорректные параметры запроса (например, недопустимая колонка сортировки или фильтра).
 * Не зависит от HTTP: в ответ 400 её превращает ExceptionMapper на уровне REST.
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
