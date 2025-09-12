package co.com.pragma.powerup.model.exceptions;

public class InvalidPaginationParametersException extends RuntimeException {
    public InvalidPaginationParametersException(String message) {
        super(message);
    }
}
