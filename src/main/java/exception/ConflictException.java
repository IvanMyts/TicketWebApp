package exception;

/** Операция противоречит текущему состоянию данных (например, удаление используемого объекта без замены). */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
