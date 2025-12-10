package movielibrary.exceptions;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entity, String property, String value) {
        super(String.format("%s with %s %s not found.", entity, property, value));
    }
}
