package pages.interfaces;

import lombok.SneakyThrows;

import java.util.Optional;

public interface Loadable extends Nameable {

    @SneakyThrows
    default void waitUntilLoaded() {
        step(getName() + " should be loaded", this::runLoadedCallback);
    }

    default boolean checkIfLoaded() {
        try {
            runLoadedCallback();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    Runnable getLoadedCallback();

    private void runLoadedCallback() {
        Optional.ofNullable(getLoadedCallback())
                .orElseThrow(() -> new WrongFrameworkUsageException(getName() + " has no @Getter-annotated loadedCallback, cannot wait for it to load"))
                .run();
    }
}
