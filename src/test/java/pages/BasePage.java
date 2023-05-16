package com.worldremit.test.web.pages;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.WebDriverRunner.url;
import static com.worldremit.test.web.browser.CookieManager.getCookie;
import static com.worldremit.test.web.browser.CookieManager.getWrCookie;
import static com.worldremit.test.web.configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;
import static io.qameta.allure.Allure.step;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.ex.ElementNotFound;
import com.google.common.collect.Streams;
import com.worldremit.test.web.configuration.EnvironmentConfig;
import com.worldremit.test.web.exceptions.EnvironmentError;
import com.worldremit.test.web.exceptions.NonexistentValueException;
import com.worldremit.test.web.exceptions.WrongFrameworkUsageException;
import com.worldremit.test.web.models.LocalizationRegion;
import com.worldremit.test.web.models.TranslationLanguage;
import com.worldremit.test.web.models.codes.CountryCodesGetter;
import com.worldremit.test.web.models.cookies.ConnectSid;
import com.worldremit.test.web.models.cookies.SelectFrom;
import com.worldremit.test.web.models.gtm.DataLayer;
import com.worldremit.test.web.models.gtm.DataLayer.DataLayerBuilder;
import com.worldremit.test.web.models.gtm.DataLayerPage;
import com.worldremit.test.web.pages.components.Header;
import pages.interfaces.HeaderInterface;
import com.worldremit.test.web.pages.interfaces.Bundleable;
import com.worldremit.test.web.pages.interfaces.ExpectedElements;
import com.worldremit.test.web.pages.interfaces.Page;
import com.worldremit.test.web.services.BffService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.openqa.selenium.By;

@Slf4j
@SuperBuilder(toBuilder = true)
public abstract class BasePage implements Page, DataLayerPage, Bundleable {

    @Getter
    private final HeaderInterface header = new Header(); // override this for non-cms pages
    @Getter(AccessLevel.PROTECTED)
    private final Map<String, String> errorPagesSignatures = Map.of(
            "404 Not Found page",
            "//h1[contains(text(), 'The page you were looking for was not found')]",
            "503 Service Unavailable page",
            "//body/p[contains(text(),'HTTP Error 503. The service is unavailable.')]",
            "400 Bad Request page",
            "//body/*/h1[contains(text(),'400 Bad Request')]",
            "Maintenance page",
            "//*[contains(@class, 'maintenance-visual')]",
            "PerimeterX Human Test Page",
            "//div/h1[contains(text(),'Please verify you are a human')]"
    );
    @Getter
    @Default
    private final TranslationLanguage pageLanguage = ENVIRONMENT_CONFIG.getLanguage();
    @Getter
    @Default
    private final LocalizationRegion localizationRegion = null;
    @Singular
    @Getter
    private final List<NameValuePair> uriParams;
    @Getter
    private final String uriFragment;
    @Getter
    @Default
    private final boolean loginNeeded = false; // if true page cannot be accessed for not logged in users
    @Getter
    private final boolean mobileView = ENVIRONMENT_CONFIG.isMobile();
    private final String baseUrl;
    /**
     * This should be overridden in child classes to hold expected values while we
     * check if a page has been fully loaded.
     */
    @Getter
    private final ExpectedElements expectedElements = ExpectedElements.builder().build();
    @Getter
    DataLayer mandatoryDataLayer;
    @Getter
    @Singular("additionalUri")
    List<URI> additionalUris;

    @Override
    public void waitUntilLoaded() {
        getErrorPagesSignatures().entrySet().stream()
                .filter(signature -> $(By.xpath(signature.getValue())).isDisplayed())
                .findFirst()
                .ifPresent(signature -> {
                    throw new EnvironmentError(signature.getKey() + " is loaded");
                });
        Page.super.waitUntilLoaded();
    }

    public URI getUri() {
        try {
            if (getUriBuilder() == null) {
                throw new WrongFrameworkUsageException("Cannot build URI for " + getName() + ": getUriBuilder() returns null");
            }
            return getUriBuilder().build();
        } catch (URISyntaxException e) {
            throw new WrongFrameworkUsageException("Cannot build URI for " + getName(), e);
        }
    }

    @Override
    public String toString() {
        return getName() + "@" + hashCode();
    }

    public DataLayer buildDataLayer() {
        return getDataLayerBuilder().build();
    }

    /**
     * Returns the baseUrl of the page, based on the default configuration. Can be be overridden
     * in child classes by changing the class field:
     *   ```
     *   private final String baseUrl = ENVIRONMENT_CONFIG.getBaseUrl("projectName");
     *   ```
     * or, on instance leve, during builder work:
     *    ```
     *    SomePage.builder().baseUrl("NEW_BASE_URL").build();
     *    ```
     */
    public String getBaseUrl() {
        return Optional.ofNullable(baseUrl).orElse(EnvironmentConfig.getDefaultBaseUrl());
    }

    public void load() {
        step("Load " + this.getName(), () -> {
            this.open();
            waitUntilLoaded();
        });
    }

    public abstract BasePageBuilder<?, ?> toBuilder();

    public boolean isIndexed() {
        this.waitUntilNavigatedTo();
        return !$("head meta[content*='noindex']").exists();
    }

    public abstract String getUriPath();

    /**
     * The method clears input field provided as parameter.
     *
     * @param formInputField SelenideElement.
     */
    public static void clearFormField(SelenideElement formInputField) {
        formInputField.shouldBe(visible).click();
        executeJSCommand("selectall");
        executeJSCommand("delete");
    }

    /**
     * The method clears all input fields from provided collection.
     *
     * @param formInputFields ElementsCollection.
     */
    public static void clearForm(ElementsCollection formInputFields) {
        formInputFields.asFixedIterable().forEach(BasePage::clearFormField);
    }

    /**
     * Convenience method that returns a language of currently opened page.
     *
     * NOTE: This does NOT reflect the expected state of the page!
     *
     * @return TranslationLanguage of currently opened page, based on URL
     */
    public static TranslationLanguage getLanguageFromCurrentUrl() {
        return TranslationLanguage.fromCode(url().replaceFirst(EnvironmentConfig.getDefaultBaseUrl(), "").substring(1, 3));
    }

    public static void smartSelectOptionByValue(SelenideElement element, String value) {
        try {
            element.shouldBe(enabled).selectOptionByValue(value);
        } catch (ElementNotFound elementNotFound) {
            val possibleValues = Streams.stream(element.findAll("option").asFixedIterable())
                    .filter(el -> !el.getValue().isEmpty())
                    .map(el -> String.format("%s (%s)", el.getValue(), el.getText()))
                    .collect(toList());
            val message = String.format("Cannot find '%s' value in options. Possible values are: %s", value, possibleValues);
            throw new NonexistentValueException(message, elementNotFound);
        }
    }

    protected URIBuilder getUriBuilder() throws URISyntaxException {
        if (!getBaseUrl().matches("^https?://.*")) {
            throw new WrongFrameworkUsageException(String.format("%s.baseUrl must start with valid http(s) scheme!", getName()));
        }
        val uriBuilder = new URIBuilder(getBaseUrl());

        if (getUriPath() == null) {
            throw new WrongFrameworkUsageException(getName() + " has no uriPath set or no @Getter for it");
        }

        val urlSegments = Arrays.stream(getUriPath().split("/"))
                .filter(not(String::isEmpty))
                .collect(toList());
        // Support for legacy pages (no language code in their URL)
        if (getPageLanguage() != null) {
            urlSegments.add(0, getPageLanguage().getCode());
        }
        if (getLocalizationRegion() != null) {
            urlSegments.set(0, getLocalizationRegion().getCode().toLowerCase());
        }
        uriBuilder.setPathSegments(urlSegments);
        uriBuilder.setFragment(getUriFragment());
        uriBuilder.addParameters(getUriParams());

        return uriBuilder;
    }

    /**
     * Child objects should override this method, call super and add any additional DataLayer
     * fields that are expected on given page.
     */
    protected DataLayerBuilder getDataLayerBuilder() {
        val header = getHeader();
        this.waitUntilLoaded();
        if (Objects.isNull(getMandatoryDataLayer())) {
            throw new WrongFrameworkUsageException(String.format(
                    "%s has missing 'mandatoryDataLayer', you must add it for dataLayer checking",
                    this.getName()));
        }
        val dataLayerBuilder = getMandatoryDataLayer().toBuilder();

        dataLayerBuilder.pageType(getMandatoryDataLayer().getPageType());
        dataLayerBuilder.pageName(getMandatoryDataLayer().getPageName());
        dataLayerBuilder.pageLanguage(getPageLanguage());
        if (Objects.nonNull(header)) {
            header.waitUntilLoaded();
            dataLayerBuilder.visitorStatus(header.isLoggedIn() ? "logged in" : "not logged in");
            if (header.isLoggedIn()) {
                // NOTE: this uses BFF, so it might be effected by the same bug as CMS itself
                val bffService = BffService.getBffService();
                val userAccount = bffService.getAccount(getCookie(ConnectSid.class));
                dataLayerBuilder.userAccount(userAccount);
            }
            // Fetch senderCountry from cookie instead of header dropdown, so it's faster
            val senderCountry = CountryCodesGetter.getCountryCode(getWrCookie(SelectFrom.class).getValue());
            dataLayerBuilder.senderCountry(senderCountry);
            dataLayerBuilder.senderCountryIso(senderCountry);
        }
        return dataLayerBuilder;
    }

    /**
     * Repeats codeBlock within BasePage instance until no error page is shown
     *
     * @param codeBlock Runnable to execute
     * @param maxAttemptCount maximum attempt count
     */
    protected void repeatUntilNoErrorPage(Runnable codeBlock, int maxAttemptCount) {
        Optional<Entry<String, String>> errorPageFound;
        Error lastException = null;
        var currentTry = 0;
        do {
            currentTry++;
            try {
                codeBlock.run();
            } catch (Error e) {
                lastException = e;
            }
            errorPageFound = getErrorPagesSignatures().entrySet().stream()
                    .filter(signature -> $(By.xpath(signature.getValue())).isDisplayed())
                    .findFirst();
            if (errorPageFound.isPresent()) {
                log.info("{} found during attempt {} out of {}.", errorPageFound.get().getKey(), currentTry, maxAttemptCount);
            }
        } while (errorPageFound.isPresent() && currentTry < maxAttemptCount);
        if (errorPageFound.isPresent()) {
            throw new AssertionError(String.format("%s found, no retry attempts left", errorPageFound.get().getKey()), lastException);
        }
        if (Objects.nonNull(lastException)) {
            throw new AssertionError(String.format("%s found, no retry attempts left", lastException.getClass().getSimpleName()), lastException);
        }
    }

    /**
     * Merges NameValuePair into current uriParams and returns it as new List.
     *
     * Useful when PO is using toBuilder() and builds new instance with different uriParams.
     *
     * If the this.uriParams already had NameValuePair with the same name as newPair, it will be replaced. If it didn't,
     * it will be added.
     *
     * @param newPair query parameters to merge
     * @return new List of NameValuePair with merged newPair
     */
    protected List<NameValuePair> mergeWithUriParams(NameValuePair newPair) {
        val mergedUriParams = this.getUriParams().stream()
                .map(currentQueryParam -> currentQueryParam.getName().equals(newPair.getName()) ? newPair : currentQueryParam)
                .collect(toList());
        if (!mergedUriParams.contains(newPair)) {
            mergedUriParams.add(newPair);
        }
        return mergedUriParams;
    }

    /**
     * @url https://developer.mozilla.org/en-US/docs/Web/API/Document/execCommand
     * @param command WebAPI command String format
     */
    private static void executeJSCommand(String command) {
        executeJavaScript(String.format("document.execCommand(\"%s\",null,false); ", command));
    }
}
