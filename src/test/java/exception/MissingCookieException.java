package exception;

public class MissingCookieException extends AssertionError {

    public MissingCookieException(final String message) {
        super(message);
    }
}
