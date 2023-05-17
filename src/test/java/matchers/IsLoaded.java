package matchers;

import com.codeborne.selenide.WebDriverRunner;
import lombok.val;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import pages.interfaces.Loadable;

import java.util.Objects;

import static io.qameta.allure.Allure.step;

public class IsLoaded extends TypeSafeMatcher<Loadable> {

    private Loadable page;
    private Throwable callbackException;

    @Override
    protected boolean matchesSafely(Loadable assertedPage) {
        page = assertedPage;
        val stepMessage = "Validate that " + (Objects.isNull(page) ? "Page" : page.getName()) + " is loaded";
        return step(stepMessage, () -> {
            try {
                page.waitUntilLoaded();
            } catch (Throwable e) {
                callbackException = e;
                return false;
            }
            return true;
        });
    }

    @Override
    public void describeTo(Description description) {
        val selfDescription = "Loaded " + (Objects.isNull(page) ? "Page" : page.getName());
        description.appendText(selfDescription);
    }

    @Override
    protected void describeMismatchSafely(Loadable assertedPage, Description mismatchDescription) {
        val callbackMsg = callbackException.getClass().getSimpleName() + ": " + callbackException.getMessage();
        mismatchDescription
                .appendText("but ")
                .appendText(page.getName())
                .appendText(" did not load (current URL: ")
                .appendText(WebDriverRunner.url())
                .appendText(")")
                .appendText(" callback threw:\n")
                .appendText(callbackMsg.replaceAll("(?m)^", "    "));
    }

    public static IsLoaded loaded() {
        return new IsLoaded();
    }

    public static IsLoaded isLoaded() {
        return loaded();
    }

}
