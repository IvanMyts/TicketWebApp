package exception;

/** Некорректные параметры запроса (например, недопустимая колонка сортировки или фильтра). */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
