package pages.interfaces;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.Stopwatch;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.val;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.qameta.allure.Allure.step;
import static matchers.IsOpened.isOpened;
import static matchers.WrMatcherAssert.assertThat;
import static uri.UriManager.compareUris;
import static uri.UriManager.getOpenedUri;

public interface Openable extends Nameable {

    /**
     * @return Main URI of this Openable
     */
    URI getUri();

    /**
     * @return List of additional URIs for this Openable. Useful, when one page is visible under several paths.
     */
    default List<URI> getAdditionalUris() {
        return Collections.emptyList();
    }

    /**
     * @return List of all URIs of this Openable. The main URI is always the first item in the returned list.
     */
    default List<URI> getAllUris() {
        return Lists.asList(getUri(), getAdditionalUris().toArray(new URI[0]));
    }

    @SneakyThrows
    default void open() {
        step(
                String.format("Open %s: %s", this.getName(), getUri().toURL()),
                () -> {
                    try {
                        Selenide.open(getUri().toURL());
                    } catch (TimeoutException timeoutException) {
                        throw new AssertionError(String.format("Could not open %s: timeout occurred", this.getName()), timeoutException);
                    } catch (WebDriverException webDriverException) {
                        throw new AssertionError(String.format("Could not open %s:", this.getName()), webDriverException);
                    }
                }
        );
    }

    /**
     * Checks for up to `Configuration.timeout` milliseconds if the currently opened URL matches
     * this objects URL. Ignores fragment part.
     * @param withQuery if true, it will take query parameters into consideration
     * @return true if URLs match, false if they do not within `Configuration.timeout` ms
     */
    @SneakyThrows
    default boolean checkIfOpened(boolean withQuery, boolean withFragment) {
        val stopwatch = new Stopwatch(Configuration.timeout);
        do {
            val openedUri = getOpenedUri(withQuery, withFragment);
            if (isUriMatching(openedUri, withQuery, withFragment)) {
                return true;
            }
            stopwatch.sleep(Configuration.pollingInterval);
        } while (!stopwatch.isTimeoutReached());
        return false;
    }

    @SneakyThrows
    default boolean checkIfOpened() {
        return checkIfOpened(false, false);
    }

    @SneakyThrows
    default void waitUntilOpened() {
        // let's "break" the Page Object Pattern approach, so we don't need to duplicate code and/or extract the matcher method as a static one
        assertThat(this, isOpened());
    }

    @SneakyThrows
    default boolean redirectedWithin(long timeoutMilliseconds) {
        val stopwatch = new Stopwatch(timeoutMilliseconds);
        do {
            val openedUrl = getOpenedUri(false, false);
            if (!openedUrl.toURL().sameFile(getUri().toURL())) { return true; }
            stopwatch.sleep(Configuration.pollingInterval);
        } while (!stopwatch.isTimeoutReached());
        return false;
    }

    default void waitUntilRedirectWithin(long timeoutMilliseconds) {
        step("Wait until redirected out of " + this.getName(), () -> {
            if (!redirectedWithin(timeoutMilliseconds)) {
                throw new AssertionError(
                        String.format("Browser did not redirect out of %s within %s seconds", getName(),
                                TimeUnit.MILLISECONDS.toSeconds(timeoutMilliseconds)));
            }
        });
    }

    @SneakyThrows
    private boolean isUriMatching(URI expectedUri, boolean withQuery, boolean withFragment) {
        return compareUris(expectedUri, getAllUris(), withQuery, withFragment);
    }
}
