package movielibrary.exceptions;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }

  public DuplicateEntityException(String entity, String property, String value) {
      super(String.format("%s with %s %s already exists.", entity, property, value));
  }
}
