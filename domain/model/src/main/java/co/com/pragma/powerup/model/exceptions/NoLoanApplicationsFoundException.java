package co.com.pragma.powerup.model.exceptions;

public class NoLoanApplicationsFoundException extends RuntimeException {
    public NoLoanApplicationsFoundException(String message) {
        super(message);
    }
}
