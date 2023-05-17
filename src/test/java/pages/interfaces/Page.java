package pages.interfaces;

import lombok.SneakyThrows;

import java.net.URI;

public interface Page extends Openable, Loadable, Viewable, FullyLoadable {

    @SneakyThrows
    default void waitUntilNavigatedTo() {
        this.waitUntilOpened();
        this.waitUntilLoaded();
    }

    @Override
    default URI getUri() {
        return null;
    }
}
