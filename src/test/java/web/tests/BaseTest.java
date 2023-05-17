package web.tests;

import configuration.EnvironmentConfig;
import configuration.WebDriverType;
import cookies.CookieManager;
import cookies.WrCookie;
import exception.WrongFrameworkUsageException;
import helpers.BuildEnvType;
import helpers.BuildEnvTypeManager;
import helpers.ProjectDetectionExtension;
import lombok.Setter;
import lombok.val;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;
import tags.E2eTest;

import java.lang.annotation.Annotation;

import java.util.List;
import java.util.Objects;


import static configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;
import static cookies.CookieManager.forDomain;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

@ExtendWith({ProjectDetectionExtension.class,
})
public abstract class BaseTest extends TearDownable {

    @Setter
    private String dummyUrl;

    /**
     * Pass-through method to simplify @DisabledIf usage (no need to include full classpath)
     */
    public static boolean isMobile() {
        return ENVIRONMENT_CONFIG.isMobile();
    }

    /**
     * Pass-through method to simplify @DisabledIf usage (no need to include full classpath)
     */
    public static boolean isDesktop() {
        return EnvironmentConfig.isDesktop();
    }

    /**
     * Detects if given test is run against the production build. To be used with @DisabledIf/@EnabledIf annotations.
     */
    public static boolean isProduction(ExtensionContext extensionContext) {
        val testTags = ProjectDetectionExtension.getAllProjectTestAnnotations(extensionContext).stream()
                .map(Annotation::annotationType)
                .filter(not(E2eTest.class::equals))
                .collect(toList());
        val buildEnvTypes = testTags.stream()
                .map(BuildEnvTypeManager::getBuildEnvTypeFor)
                .distinct()
                .collect(toList());
        Assumptions.assumeFalse(
                buildEnvTypes.size() > 1,
                String.format(
                        "Cannot determine BuildEnvType for %s - they are not aligned: %s",
                        testTags.stream().map(Class::getSimpleName).collect(toList()),
                        buildEnvTypes));
        return buildEnvTypes.get(0).equals(BuildEnvType.PRD);
    }

    /**
     * Pass-through method to simplify @DisabledIf usage (no need to include full classpath)
     */
    public static boolean isSandbox() {
        return EnvironmentConfig.isSandbox();
    }

    public static boolean isHeadless() {
        return ENVIRONMENT_CONFIG.getWebDriverType().equals(WebDriverType.HEADLESS);
    }

    protected void addCookie(WrCookie wrCookie) {
        forDomain(getDummyUrl()).addCookie(wrCookie);
    }

    protected void addCookie(Cookie cookie) {
        forDomain(getDummyUrl()).addCookie(cookie);
    }

    protected boolean isCookieSet(WrCookie wrCookie) {
        return CookieManager.forDomain(getDummyUrl()).isCookieSet(wrCookie);
    }

    protected boolean isCookieSet(Class<? extends WrCookie> wrCookieClass) {
        return CookieManager.forDomain(getDummyUrl()).isCookieSet(wrCookieClass);
    }

    protected void setCookie(WrCookie wrCookie) {
        CookieManager.forDomain(getDummyUrl()).setCookie(wrCookie);
    }

    protected void setCookie(Cookie cookie) {
        CookieManager.forDomain(getDummyUrl()).addCookie(cookie);
    }

    protected void deleteCookie(Class<? extends WrCookie> wrCookieClass) {
        CookieManager.forDomain(getDummyUrl()).deleteCookie(wrCookieClass);
    }

    protected Cookie getCookie(Class<? extends WrCookie> wrCookieClass) {
        return CookieManager.forDomain(getDummyUrl()).getCookie(wrCookieClass);
    }

    protected String getCookieValue(Class<? extends WrCookie> wrCookieClass) {
        return CookieManager.forDomain(getDummyUrl()).getCookieValue(wrCookieClass);
    }

    protected WrCookie getWrCookie(Class<? extends WrCookie> wrCookieClass) {
        return CookieManager.forDomain(getDummyUrl()).getWrCookie(wrCookieClass);
    }

    protected void setAndValidateCookie(WrCookie wrCookie) {
        CookieManager.forDomain(getDummyUrl()).setAndValidateCookie(wrCookie);
    }

    protected void setAndValidateCookies(WrCookie... wrCookies) {
        CookieManager.forDomain(getDummyUrl()).setAndValidateCookies(wrCookies);
    }

    protected void setAndValidateCookie(List<WrCookie> wrCookie) {
        CookieManager.forDomain(getDummyUrl()).setAndValidateCookies(wrCookie);
    }

    protected void disableTestIf(boolean condition, String reason) {
        Assumptions.assumeFalse(condition, reason);
    }

    private String getDummyUrl() {
        if (Objects.isNull(dummyUrl)) {
            throw new WrongFrameworkUsageException("The test context appears to not be found");
        }
        return dummyUrl;
    }
}
