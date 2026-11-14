package exception;

/** Операция противоречит текущему состоянию данных (например, удаление используемого объекта без замены). На уровне REST превращается в 409. */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
