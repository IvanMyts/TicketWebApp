package exception;

/** Объект с указанным id не найден. На уровне REST превращается в 404. */
public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String message) {
        super(message);
    }
}
