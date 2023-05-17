package waiters;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Stopwatch;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import lombok.val;
import matchers.WrMatcherAssert;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import java.util.function.Supplier;

import static configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;

/**
 * Waiter class that uses hamcrest matchers and assertions. Useful in cases not covered by Selenide.should* methods.
 *
 * Example usage:
 *
 *      // waiter will repeat assertThat until it passes
 *      waitUntil("asyncList check", asyncList, contains("element_that_will_show_up_eventually")));
 *
 *      // waiter will call atBottom and use not(T value) matcher (i.e. it will wait up to 1000ms until atBottom returns false)
 *      waitUntilCall("Scroll check", Selenide::atBottom, not(true), 1000);
 *
 *      // waiter will call getSomeValue until it matches "expected_value"
 *      waitUntilCall(() -> someApiService.getSomeValue, equalTo("expected_value"));
 *
 * In case of timeout, the most recent AssertionError instance is thrown.
 *
 */
public class AssertionWaiter {
    public static <T> void waitUntil(T actual, Matcher<? super T> matcher) {
        waitUntil(actual, matcher, ENVIRONMENT_CONFIG.getTimeout());
    }

    public static <T> void waitUntil(T actual, Matcher<? super T> matcher, long timeoutMilliseconds) {
        waitUntil("", actual, matcher, timeoutMilliseconds);
    }

    public static <T> void waitUntil(String reason, T actual, Matcher<? super T> matcher) {
        waitUntil(reason, actual, matcher, ENVIRONMENT_CONFIG.getTimeout());
    }

    public static <T> void waitUntil(String reason, T actual, Matcher<? super T> matcher, long timeoutMilliseconds) {
        waitUntilCall(reason, () -> actual, matcher, timeoutMilliseconds, Configuration.pollingInterval);
    }

    @SneakyThrows
    public static <T> void waitUntilCall(Supplier<T> supplier, Matcher<? super T> matcher) {
        waitUntilCall("", supplier, matcher, ENVIRONMENT_CONFIG.getTimeout(), Configuration.pollingInterval);
    }

    @SneakyThrows
    public static <T> void waitUntilCall(String reason, Supplier<T> supplier, Matcher<? super T> matcher) {
        waitUntilCall(reason, supplier, matcher, ENVIRONMENT_CONFIG.getTimeout(), Configuration.pollingInterval);
    }

    public static <T> void waitUntilCall(Supplier<T> supplier, Matcher<? super T> matcher, long timeoutMilliseconds) {
        waitUntilCall("", supplier, matcher, timeoutMilliseconds, Configuration.pollingInterval);
    }

    @SneakyThrows
    public static <T> void waitUntilCall(String reason, Supplier<T> supplier, Matcher<? super T> matcher, long timeoutMilliseconds,
                                         long pollingMilliseconds) {
        val stopwatch = new Stopwatch(timeoutMilliseconds);
        AssertionError lastError;
        do {
            try {
                T actual = supplier.get();
                if (Strings.isNullOrEmpty(reason)) {
                    MatcherAssert.assertThat(reason, actual, matcher);
                } else {
                    WrMatcherAssert.assertThat(reason, actual, matcher);
                }
                return;
            } catch (AssertionError error) {
                lastError = error;
            }
            stopwatch.sleep(pollingMilliseconds);
        } while (!stopwatch.isTimeoutReached());
        throw lastError;
    }
}
