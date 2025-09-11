package co.com.pragma.powerup.model.exceptions;

public class LoanApplicationRepositoryException extends RuntimeException {
    public LoanApplicationRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
