package cookies;

import com.codeborne.selenide.Stopwatch;
import com.codeborne.selenide.WebDriverRunner;
import exception.MissingCookieException;
import exception.WrongFrameworkUsageException;
import io.qameta.allure.Step;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.openqa.selenium.Cookie;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static browser.Browser.isBrowserOpen;
import static browser.Browser.runInNewTab;
import static com.codeborne.selenide.Selenide.refresh;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;
import static framework.collectors.WrCollectors.onlyOne;
import static io.qameta.allure.Allure.step;
import static java.lang.String.format;
import static java.util.function.Predicate.not;
import static matchers.WrMatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

@Slf4j
public class CookieManager {

   /* public static ContextAwareCookieManager forContext(Class<? extends Annotation> testTag) {
        return new ContextAwareCookieManager(testTag);
    }  */

    public static ContextAwareCookieManager forDomain(String urlToSetCookieOn) {
        return new ContextAwareCookieManager(urlToSetCookieOn);
    }

    @Step("Add {0} cookie")
    public static void addCookie(WrCookie wrCookie) {
        addCookie(wrCookie.toCookie());
    }

    public static void addCookie(Cookie cookie) {
        log.info("Add cookie: " + cookie);
        WebDriverRunner.getWebDriver().manage().addCookie(cookie);
    }

    public static boolean isCookieSet(@NonNull WrCookie wrCookie) {
        if (!isBrowserOpen()) {
            return false;
        }
        try {
            val actualCookie = getWrCookie(wrCookie.getClass());
            return wrCookie.equals(actualCookie);
        } catch (MissingCookieException e) {
            return false;
        }
    }

    public static boolean isCookieSet(Class<? extends WrCookie> wrCookieClass) {
        if (!isBrowserOpen()) {
            return false;
        }
        try {
            getWrCookie(wrCookieClass);
            return true;
        } catch (MissingCookieException e) {
            return false;
        }
    }

    public static void setCookie(WrCookie wrCookie) {
        deleteCookie(wrCookie.getClass());
        addCookie(wrCookie);
    }

    public static void setCookie(Cookie cookie) {
        getWebDriver().manage().deleteCookieNamed(cookie.getName());
        addCookie(cookie);
    }

    public static void deleteCookie(Class<? extends WrCookie> wrCookieClass) {
        log.info("Delete cookie: " + wrCookieClass.getSimpleName());
        step(format("Delete %s cookie", wrCookieClass.getSimpleName()), () -> {
            val cookieName = getWrCookieName(wrCookieClass);
            getWebDriver().manage().deleteCookieNamed(cookieName);
        });
    }

    public static WrCookie getWrCookie(Class<? extends WrCookie> wrCookieClass) {
        val cookieValue = getCookieValue(wrCookieClass);
        return newCookie(wrCookieClass, cookieValue);
    }

    public static Cookie getCookie(Class<? extends WrCookie> wrCookieClass) {
        return step(format("Get %s cookie", wrCookieClass.getSimpleName()), () -> {
            val cookieName = getWrCookieName(wrCookieClass);
            val matchingCookie = getWebDriver().manage().getCookies().stream()
                    .filter(cookie -> Objects.equals(cookie.getName(), cookieName))
                    .collect(onlyOne(cookieName + " cookie", MissingCookieException::new));
            return matchingCookie;
        });
    }

    public static String getCookieValue(Class<? extends WrCookie> wrCookieClass) {
        return getCookie(wrCookieClass).getValue();
    }

    public static void setAndValidateCookie(WrCookie wrCookie) {
        setAndValidateCookies(wrCookie);
    }

    public static void setAndValidateCookies(WrCookie... wrCookies) {
        setAndValidateCookies(Arrays.asList(wrCookies));
    }

    @Step("Set and validate cookies: {0}")
    public static void setAndValidateCookies(List<WrCookie> wrCookies) {
        val stopwatch = new Stopwatch(ENVIRONMENT_CONFIG.getTimeout());
        val pollingIntervalMs = 1_000;
        AssertionError lastError;
        // do-while loop needed as there are random cases when cookies being set have "old" values after page refresh
        do {
            try {
                wrCookies.forEach(wrCookie -> {
                    if (!wrCookie.isSet()) { CookieManager.setCookie(wrCookie); }
                });
                refresh();
                assertThat(
                        "No required cookies should be left unset",
                        wrCookies.stream().filter(not(WrCookie::isSet)).collect(Collectors.toList()),
                        empty());
                return;
            } catch (AssertionError error) {
                lastError = error;
            }
            stopwatch.sleep(pollingIntervalMs);
        } while (!stopwatch.isTimeoutReached());
        throw lastError;
    }

    private static String getWrCookieName(Class<? extends WrCookie> wrCookieClass) {
        return newCookie(wrCookieClass, "").getName();
    }

    private static WrCookie newCookie(Class<? extends WrCookie> wrCookieClass, String value) {
        try {
            val constructor = wrCookieClass.getConstructor(String.class);
            return constructor.newInstance(value);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new WrongFrameworkUsageException("Object implementing WrCookie must have public String-constructor (best to add via "
                    + "@RequiredArgsConstructor)", e);
        }
    }

    public static class ContextAwareCookieManager {

        private final String dummyUrl;

        public ContextAwareCookieManager(final String dummyUrl) {
            this.dummyUrl = dummyUrl;
        }

        public void addCookie(WrCookie wrCookie) {
            runInNewTab(() -> CookieManager.addCookie(wrCookie), dummyUrl);
        }

        public void addCookie(Cookie cookie) {
            runInNewTab(() -> CookieManager.addCookie(cookie), dummyUrl);
        }

        public boolean isCookieSet(WrCookie wrCookie) {
            return runInNewTab(() -> CookieManager.isCookieSet(wrCookie), dummyUrl);
        }

        public boolean isCookieSet(Class<? extends WrCookie> wrCookieClass) {
            return runInNewTab(() -> CookieManager.isCookieSet(wrCookieClass), dummyUrl);
        }

        public void setCookie(WrCookie wrCookie) {
            runInNewTab(() -> CookieManager.setCookie(wrCookie), dummyUrl);
        }

        public void setCookie(Cookie cookie) {
            runInNewTab(() -> CookieManager.setCookie(cookie), dummyUrl);
        }

        public void deleteCookie(Class<? extends WrCookie> wrCookieClass) {
            runInNewTab(() -> CookieManager.deleteCookie(wrCookieClass), dummyUrl);
        }

        public WrCookie getWrCookie(Class<? extends WrCookie> wrCookieClass) {
            return runInNewTab(() -> CookieManager.getWrCookie(wrCookieClass), dummyUrl);
        }

        public Cookie getCookie(Class<? extends WrCookie> wrCookieClass) {
            return runInNewTab(() -> CookieManager.getCookie(wrCookieClass), dummyUrl);
        }

        public String getCookieValue(Class<? extends WrCookie> wrCookieClass) {
            return runInNewTab(() -> CookieManager.getCookieValue(wrCookieClass), dummyUrl);
        }

        public void setAndValidateCookies(List<WrCookie> wrCookies) {
            runInNewTab(() -> CookieManager.setAndValidateCookies(wrCookies), dummyUrl);
        }

        public void setAndValidateCookies(WrCookie... wrCookies) {
            runInNewTab(() -> CookieManager.setAndValidateCookies(wrCookies), dummyUrl);
        }

        public void setAndValidateCookie(WrCookie wrCookie) {
            runInNewTab(() -> CookieManager.setAndValidateCookie(wrCookie), dummyUrl);
        }

        private String getWrCookieName(Class<? extends WrCookie> wrCookieClass) {
            return runInNewTab(() -> CookieManager.getWrCookieName(wrCookieClass), dummyUrl);
        }

    }
}
