package configuration;

import com.codeborne.selenide.Configuration;
import exception.WrongFrameworkUsageException;
import helpers.BuildEnvType;
import joptsimple.internal.Strings;
import lombok.val;
import models.codes.TranslationLanguage;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Converter;
import pages.interfaces.ViewType;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"classpath:${executionEnvironment}.properties", "classpath:common.properties", "classpath:secrets.properties"})
public interface EnvironmentConfig extends Config {

    EnvironmentConfig ENVIRONMENT_CONFIG = ConfigFactory.create(EnvironmentConfig.class, System.getProperties(), System.getenv());

    String PROD_URL = "https://www.ourentity.com/";
    String PIPELINE_HEADLESS_SANDBOX_URL = "web-proxy";
    String PIPELINE_MOON_SANDBOX_URL = "mybox.com";
    String LOCAL_SANDBOX_URL = "host.docker.internal";
    String PROJECT_VERSION_PATH_KEY = "path.%s.version";
    String FEATURE_TOGGLE_PATH_KEY = "path.%s.featureToggles";
    String DEFAULT_PARALLELISM = "4";

    // ----------------------------------------------
    // ---- ---- General site configuration ---- ----
    // ----------------------------------------------

    /**
     * Main URL of the project under test
     */
    @Key("url.%s.website")
    String getBaseUrl(String projectName);

    /**
     * Path of endpoints that exposes version in JSON format
     */
    @Key(PROJECT_VERSION_PATH_KEY)
    String getVersionPath(String projectName);

    /**
     * Path of endpoints that exposes feature toggles in JSON format
     */
    @Key(FEATURE_TOGGLE_PATH_KEY)
    String getFeatureTogglesPath(String projectName);

    /**
     * Default language of the website
     *
     */
    @Key("website.language")
    @DefaultValue("en")
    @ConverterClass(TranslationLanguageConverter.class)
    TranslationLanguage getLanguage();

    @Key("url.service.legacyApi")
    String getLegacyApiServiceUrl();

    @Key("url.service.mercuryApi")
    String getMercuryServiceUrl();

    @Key("url.service.loyaltyApi")
    String getLoyaltyServiceUrl();

    @Key("url.service.nhTemplateApi")
    String getNhTemplateServiceUrl();

    @Key("url.service.contentfulApi")
    String getContentfulServiceUrl();

    @Key("secret.service.contentfulApi")
    String getContentfulSecret();

    @Key("url.service.transferStateChangeService")
    String getTransactionStatusServiceUrl();

    @Key("url.service.greenhouseApi")
    String getGreenhouseServiceUrl();

    @Key("url.service.recipientChangeServiceApi")
    String getRecipientChangeServiceUrl();

    @Key("authorization.service.recipientChangeServiceApi")
    String getRecipientChangeServiceAuthorizationToken();

    @Key("url.service.submissionFlowFacadeApi")
    String getSubmissionFlowFacadeUrl();

    @Key("url.service.requestMoneyApi")
    String getRequestMoneyUrl();

    @Key("secret.service.requestMoneyApi.jazzCash")
    String getRequestMoneyApiJazzCashSecret();

    @Key("secret.service.requestMoneyApi.zenithBank")
    String getRequestMoneyApiZenithBankSecret();

    @Key("url.service.operationsManagementApi")
    String getOperationsManagementServiceUrl();

    @Key("url.service.cognitoApi")
    String getCognitoApi();

    @Key("url.service.manualBankTransferService")
    String getManualBankTransferServiceUrl();

    @Key("host.redis")
    String getRedisHost();

    // -------------------------------------------
    // ---- ---- Framework configuration ---- ----
    // -------------------------------------------

    /**
     * Default timeout for all Selenide actions
     */
    @Key("selenide.element.timeout")
    @DefaultValue("2000")
    Integer getTimeout();

    /**
     * The "heavy" timeout, used mainly for legacy projects, for which
     * the regular timeout is too small.
     */
    @Key("selenide.element.heavyPagesTimeout")
    @DefaultValue("4000")
    Integer getHeavyPagesTimeout();

    @Key("selenide.element.paymentProcessingTimeout")
    @DefaultValue("60000")
    Integer getPaymentProcessingTimeout();

    // -------------------------------------------
    // ---- ---- Webdriver configuration ---- ----
    // -------------------------------------------

    /**
     * Sets the maximum amount of parallel tests based on resources allocated in Moon via pipeline.
     *
     * If there are no resources allocated in pipeline, default value is used.
     */
    @DefaultValue(DEFAULT_PARALLELISM)
    @Key("moonSessionCount")
    int getMoonSessionCount();

    /**
     * Sets the timeout for browser allocation in Moon Cloud, in minutes
     */
    @DefaultValue("10")
    @Key("browserAllocationTimeout")
    int getBrowserAllocationTimeout();

    /**
     * Enables VNC support for the browser container within Selenoid UI.
     * Applicable only for remote WebDrivers (i.e. Moon/Selenoid)
     */
    @Key("webdriver.remote.enable.vnc")
    @DefaultValue("true")
    Boolean isVncEnabled();

    /**
     * Enables video-recording diagnostic feature.
     * Applicable only for remote WebDrivers (i.e. Moon/Selenoid)
     */
    @Key("webdriver.remote.enable.video")
    @DefaultValue("false")
    Boolean isVideoEnabled();

    /**
     * Sets WebDriverType to be used in testing
     *
     */
    @Key("webdriver.type")
    @DefaultValue("NATIVE")
    WebDriverType getWebDriverType();

    /**
     * Get Moon Cloud user name
     */
    @DefaultValue("worldremit")
    @Key("MOON_CLOUD_USER")
    String getMoonUser();

    /**
     * Get Moon Cloud user password
     */
    @Key("MOON_CLOUD_PASS")
    String getMoonPassword();

    /**
     * Forces mobile-view for all tests
     */
    @Key("webdriver.browser.mobile")
    @DefaultValue("false")
    boolean isMobile();

    /**
     * Configures which browser is used in testing.
     */
    @DefaultValue("chrome")
    @Key("webdriver.browser.name")
    String getBrowserName();

    /**
     * Configures which browser version is used in testing.
     *
     * NOTES:
     * - This should point the most recent version that is available in Selenoid/Moon containers.
     *   It can be easily checked by running, e.g.: docker pull selenoid/vnc_chrome:94.0 to see if it's available
     *   Selenoid images are not updated as quickly as Chrome is, thus we'll always be a little behind.
     *   Similar issue occurs for *-native configs, as our IT-managed machines tend to be a little behind
     *   Chrome release schedule as well.
     * - For headless builds, usually we need to replace this value in proper props file (*-headless.properties),
     *   because the headless containers always install the most recent Chrome version, which usually is a little
     *   ahead of the two cases mentioned above.
     */
    @DefaultValue("97")
    @Key("webdriver.browser.version")
    String getBrowserVersion();

    /**
     * Configures which locale and Accept-Language header is used in testing.
     */
    @Key("webdriver.browser.locale")
    @DefaultValue("en-GB")
    String getBrowserLocale();

    // --------------------------------
    // ---- ---- Prod secrets ---- ----
    // --------------------------------

    @Key("secret.cm.prod.%s")
    String getCmProdSecret(String secretProperty);

    @Key("secret.website.prod.%s")
    String getWebsiteProdSecret(String secretProperty);

    // ----------------------------------
    // ---- ---- Helper methods ---- ----
    // ----------------------------------

    /**
     * Facade method for obtaining the baseUrl of currently tested project.
     * Available only after ProjectDetectionExtension has finished its work.
     */
    static String getDefaultBaseUrl() {
        if (Configuration.baseUrl.equals("http://localhost:8080")) {
            throw new WrongFrameworkUsageException(
                    "Selenide-default baseUrl detected! Do not call getDefaultBaseUrl() before WebDriverConfigExtension has finished its work");
        }
        return Configuration.baseUrl;
    }

    static String getDomainName() {
        return EnvironmentConfig.getDefaultBaseUrl().replaceAll("^https?://", "");
    }

    static boolean isProduction() {
        return Optional.ofNullable(System.getenv("executionEnvironment"))
                .orElse("EXECUTION ENVIRONMENT MISSING")
                .contains("production");
    }

    /**
     * We don't run tests against DEV, thus non-production can be considered TST.
     */
    static BuildEnvType getBuildEnvType() {
        return isProduction() ? BuildEnvType.PRD : BuildEnvType.TST;
    }

    static boolean isSandbox() {
        return isLocalSandbox() || isPipelineSandbox();
    }

    static boolean isLocalSandbox() {
        return System.getenv("executionEnvironment").contains("local-sandbox");
    }

    static boolean isPipelineSandbox() {
        return System.getenv("executionEnvironment").contains("pipeline-sandbox");
    }

    static void validateSandboxRun(String customMessagePrefix) {
        val messagePrefix = Strings.isNullOrEmpty(customMessagePrefix) ? "" : customMessagePrefix + ": ";
        if (!EnvironmentConfig.isSandbox()) {
            throw new WrongFrameworkUsageException(String.format(
                    "%sNon-sandbox environment detected. Expected to find %s, %s or %s. " +
                            "Found: %s - please verify your environment setting. Currently using: %s",
                    messagePrefix,
                    EnvironmentConfig.LOCAL_SANDBOX_URL,
                    EnvironmentConfig.PIPELINE_HEADLESS_SANDBOX_URL,
                    EnvironmentConfig.PIPELINE_MOON_SANDBOX_URL,
                    EnvironmentConfig.getDefaultBaseUrl(),
                    System.getenv("executionEnvironment")));
        }
    }

    static boolean isDesktop() {
        return !ENVIRONMENT_CONFIG.isMobile();
    }

    static ViewType getViewType() {
        return ENVIRONMENT_CONFIG.isMobile() ? ViewType.MOBILE : ViewType.DESKTOP;
    }

    static Duration getLongTimeout() {
        return Duration.of(ENVIRONMENT_CONFIG.getHeavyPagesTimeout(), ChronoUnit.MILLIS);
    }

    class TranslationLanguageConverter implements Converter<TranslationLanguage> {

        @Override
        public TranslationLanguage convert(final Method method, final String code) {
            return TranslationLanguage.fromCode(code);
        }
    }

}
