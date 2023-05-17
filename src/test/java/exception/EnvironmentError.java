package exception;

/**
 * Exception that is categorized as "Environment error" in allure report
 */
public class EnvironmentError extends RuntimeException {

    public EnvironmentError(final String message) {
        super(message);
    }
}
