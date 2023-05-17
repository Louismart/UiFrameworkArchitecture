package matchers;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;

import static io.qameta.allure.Allure.step;

public class WrMatcherAssert {

    public static <T> void assertThat(T actual, Matcher<? super T> matcher) {
        step("Perform assertion via " + matcher.getClass().getSimpleName() + " on " + actual.getClass().getSimpleName(), () ->
                MatcherAssert.assertThat("", actual, matcher)
        );
    }

    public static <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
        step(reason, () ->
                MatcherAssert.assertThat(reason, actual, matcher)
        );
    }

    public static void assertThat(String reason, boolean assertion) {
        step(reason, () ->
                MatcherAssert.assertThat(reason, assertion)
        );
    }
}
