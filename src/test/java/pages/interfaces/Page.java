package pages.interfaces;

import lombok.SneakyThrows;

public interface Page extends Openable, Loadable, Viewable, FullyLoadable {

    @SneakyThrows
    default void waitUntilNavigatedTo() {
        this.waitUntilOpened();
        this.waitUntilLoaded();
    }
}
