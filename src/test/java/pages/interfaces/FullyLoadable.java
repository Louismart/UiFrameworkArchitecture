package pages.interfaces;

import exception.WrongFrameworkUsageException;

public interface FullyLoadable extends Nameable {

    ExpectedElements getExpectedElements();

    default void shouldBeFullyLoaded() {
        if (getExpectedElements().isEmpty()) {
            throw new WrongFrameworkUsageException(
                    String.format("%s is expected to fully load, but no ExpectedElements have been set!", getName()));
        }
        getExpectedElements().shouldBeFullyLoaded();
    }
}
