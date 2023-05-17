package exception;

public class WrongFrameworkUsageException extends RuntimeException {

    public WrongFrameworkUsageException(String message, Throwable originalException) {
        super(message, originalException);
    }

    public WrongFrameworkUsageException(String message) {
        super(message);
    }

}
