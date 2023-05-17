package exception;

public class NonexistentValueException extends AssertionError {

    public NonexistentValueException(String message, Throwable originalException) {
        super(message, originalException);
    }

    public NonexistentValueException(String message) {
        super(message);
    }
}
