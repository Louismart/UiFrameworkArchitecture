package matchers;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Stopwatch;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.val;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import pages.interfaces.Openable;

import java.net.URI;
import java.util.List;

import static configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;
import static io.qameta.allure.Allure.step;
import static java.util.Objects.isNull;
import static srings.StringManipulationManager.formatAsHumanReadableList;

/**
 * This is custom hamcrest matcher that is used to check whether or not the correct page is open. See IsOpened.opened
 * method for usage examples.
 *
 * NOTE: this custom matcher can be used as a reference for designing new ones.
 *
 * To implement another custom matcher, extend BaseMatcher (or its ancestor) and override:
 * - matches method: it is fed with item being asserted (i.e. the first argument to assertThat) and returns boolean
 *                   value (false will result in AssertionError). It's useful to save some state within this method
 *                   (like the `page` below), so it can be used for better messages in describe* methods.
 * - describeTo method: it is fed with current description of the Expected value on which append* method should be used,
 *                      in most cases it will be appendText.
 * It's very useful to also override:
 * - describeMismatch method: it is fed with current description of the Actual value on which append* method should be
 *                            used, in most cases it will be appendText. Keep in mind that if matches() saved some state
 *                            it can be used here.
 * NOTE: do not assume any order of execution for those methods. i.e. `describeTo` must be able to be called before
 *       `matches` has been called, so protect the code from NullPointerException!
 *
 *
 * In this particular case, TypeSafeMatcher is used, so only non-null page objects extending BasePage can be asserted
 * using it. Because of that, the matchesSafely and describeMismatchSafely methods are overridden instead of the above.
 */
public class IsOpened extends TypeSafeMatcher<Openable> {
    private Openable page;
    private URI openedUri;
    @Accessors(chain = true)
    @Setter
    private boolean queryNeeded;
    @Accessors(chain = true)
    @Setter
    private boolean fragmentNeeded;
    private List<URI> expectedUris;
    @Accessors(chain = true)
    @Setter
    private long timeoutMilliseconds = ENVIRONMENT_CONFIG.getHeavyPagesTimeout();
    private long pollingIntervalMilliseconds = Configuration.pollingInterval;

    /**
     * This is main matcher method, the assertedPage param comes from first argument of assertThat()
     */
    @Override
    @SneakyThrows
    protected boolean matchesSafely(Openable assertedPage) {
        page = assertedPage;
        val stepMessage = "Validate that " + (isNull(page) ? "Page" : page.getName()) + " is opened";
        return step(
                stepMessage,
                () -> matchUris(page.getAllUris()));
    }

    /**
     * This method is used to build the Expected part of fail message. Example result:
     * <p>
     * java.lang.AssertionError:
     * Expected: HomePage (https://staging.worldremit.com/en) opened   <--- the Expected part
     * but: was wrong URL: https://staging.worldremit.com/error   <--- the Actual part
     * <p>
     * The description already contains the "Expected: " String, to which this method appends the rest of the message.
     * All this hassle is because matchers can be mixed, i.e. the assertThat(homePage, is(opened())) will utilize
     * the describe* method from is() matcher, so the Expected part will contain "Expected: is " String instead.
     * <p>
     * NOTE: when used in "combining" matchers like allOf, anyOf, the "Expected" part is prepended to the "Actual"
     * part and both are shown in "Actual" part. More details at https://github.com/hamcrest/JavaHamcrest/issues/288
     * Here's how it looks like when combined result is shown:
     * <p>
     * Expected: (HomePage (https://staging.worldremit.com/en) opened and SomeOtherMatcherDescribeToResult)
     * but: HomePage (https://staging.worldremit.com/en) opened was wrong URL: https://staging.worldremit.com/error
     */
    @SneakyThrows
    @Override
    public void describeTo(Description description) {
        String selfDescription;
        if (page == null) {
            selfDescription = "Page (with proper URI)";
        } else {
          selfDescription =
                   page.getName() + " (URI: " + formatAsHumanReadableList(expectedUris, "or") + ")";
        }
        selfDescription += " opened";
        description.appendText(selfDescription);
        }

        /**
         * This method is used to build the Actual part of fail message. Example result:
         *
         * java.lang.AssertionError:
         * Expected: HomePage (https://staging.worldremit.com/en) opened   <--- the Expected part
         *      but: was wrong URL: https://staging.worldremit.com/error   <--- the Actual part
         *
         */
        @Override
        protected void describeMismatchSafely(){
            mismatchDescription.appendText("was wrong URL: ").appendText(openedUri.toURL().toString());
        }

        /**
         * Creates matcher for checking the opened page URL value. Matcher will poll the current url value each
         * Configuration.pollingInterval milliseconds and return false match if it doesn't match within
         * Configuration.timeoutMilliseconds.
         *
         * Example usage:
         *  assertThat(homePage, is(opened()))
         *  assertThat(homePage, is(not(opened())))
         *
         *  NOTE: even though the assertThat(homePage, opened()) would work, the whole goal of hamcrest is to produce
         *  meaningful sentences with the asserts, thus the static isOpened method below is implemented as well.
         *  This method is implemented as a main one instead of isOpened, so we won't have to use language monstrosities
         *  like assertThat(homePage, not(isOpened)).
         * @return IsOpened() matcher
         */
        public static IsOpened opened() {
            return new IsOpened();
        }

        /**
         * Same as opened() - this is syntactic sugar to simplify asserts -> assertThat(page, isOpened()).
         */
        public static IsOpened isOpened() {
            return opened();
        }

        private boolean matchUris (List<URI> uris) {
            expectedUris = uris;
            val stopwatch = new Stopwatch(timeoutMilliseconds);
            do {
                openedUri = getOpenedUri(queryNeeded, fragmentNeeded);
                if (compareUris(openedUri, expectedUris, queryNeeded, fragmentNeeded)) {
                    return true;
                }
                stopwatch.sleep(pollingIntervalMilliseconds);
            }
            while (!stopwatch.isTimeoutReached());
            return false;
        }
    }
}
