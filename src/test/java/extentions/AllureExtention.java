package extentions;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.common.io.ByteSource;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.opentest4j.TestAbortedException;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static java.util.logging.Level.WARNING;

@Slf4j
public class AllureExtention implements BeforeAllCallback, AfterTestExecutionCallback, AfterAllCallback {

    private static final int SCREENSHOT_SCROLL_TIMEOUT = 1000;
    private static final AShot SCREENSHOT_FACTORY = new AShot()
            .shootingStrategy(ShootingStrategies.viewportPasting(SCREENSHOT_SCROLL_TIMEOUT));

    private final List<Map.Entry<String, Runnable>> diagnostics = List.of(
            Map.entry("Console logs", AllureExtention::collectLogs),
            Map.entry("Browser cookies", AllureExtention::collectCookies),
            // Diagnostics below may change current window in webdriver
            Map.entry("Current URLs", AllureExtention::collectUrls),
            Map.entry("Pages source codes", AllureExtention::collectSourceCodes),
            Map.entry("Screenshots", AllureExtention::collectScreenshots) // keep this one last as it interacts with page! (scrolling)
    );

    @Override
    public void beforeAll(final ExtensionContext context) throws Exception {
        SelenideLogger.addListener("WrSelenideListener", new WrSelenideListener());
    }

    /**
     * This should detect the initialisation fails (i.e. in BeforeAll fixture)
     * However, there are issues with this: QC-2210
     */
    @Override
    public void afterAll(final ExtensionContext context) {
        checkAndRunDiagnostics(context);
    }

    @Override
    public void afterTestExecution(final ExtensionContext context) {
        checkAndRunDiagnostics(context);
    }

    public static byte[] getFullScreenshot() {
        try {
            val screenshotImage = SCREENSHOT_FACTORY
                    .takeScreenshot(getWebDriver())
                    .getImage();
            val baos = new ByteArrayOutputStream();
            ImageIO.write(screenshotImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getViewPortScreenshot() {
        return ((TakesScreenshot) getWebDriver()).getScreenshotAs(OutputType.BYTES);
    }

    public static void collectScreenshots() {
        getWebDriver().getWindowHandles().stream()
                .map(AllureExtention::getScreenshotTripleOf)
                .forEach(AllureExtention::attachScreenshotTriple);
    }

    private void checkAndRunDiagnostics(final ExtensionContext context) {
        val optionalException = context.getExecutionException();
        boolean isTestFailed = optionalException.isPresent() && !(optionalException.get() instanceof TestAbortedException);
        if (isTestFailed) {
            diagnostics.forEach(AllureExtention::runDiagnostic);
        }
    }

    private static Triple<String, byte[], byte[]> getScreenshotTripleOf(String windowHandle) {
        getWebDriver().switchTo().window(windowHandle);
        return Triple.of(getWebDriver().getTitle(), getViewPortScreenshot(), getFullScreenshot());
    }

    @SneakyThrows
    private static void attachScreenshotTriple(Triple<String, byte[], byte[]> titleAndScreenshots) {
        val title = titleAndScreenshots.getLeft();
        val viewPortScreenshot = titleAndScreenshots.getMiddle();
        val fullScreenshot = titleAndScreenshots.getRight();
        Allure.addAttachment(
                String.format("View-port screenshot for '%s'", title),
                "image/png",
                ByteSource.wrap(viewPortScreenshot).openStream(),
                "png");
        Allure.addAttachment(
                String.format("Full-view screenshot for '%s'", title),
                "image/png",
                ByteSource.wrap(fullScreenshot).openStream(),
                "png");
    }

    @SuppressWarnings("UnusedReturnValue")
    @Attachment(value = "Browser console log", type = "text/plain")
    private static String collectLogs() {
        return getConsoleLogs().stream()
                .map(LogEntry::toString)
                .collect(Collectors.joining("\n"));
    }

    @SuppressWarnings("UnusedReturnValue")
    @Attachment(value = "Currently opened URLs", type = "text/plain")
    private static String collectUrls() {
        return getWebDriver().getWindowHandles().stream()
                .map(handle ->
                        String.format(
                                "%s - '%s'",
                                getWebDriver().switchTo().window(handle).getCurrentUrl(),
                                getWebDriver().getTitle()))
                .collect(Collectors.joining("\n"));
    }

    private static void collectSourceCodes() {
        getWebDriver().getWindowHandles()
                .forEach(windowHandle -> {
                    getWebDriver().switchTo().window(windowHandle);
                    Allure.addAttachment(
                            String.format("Source code for '%s'", getWebDriver().getTitle()),
                            "text/html",
                            getWebDriver().getPageSource(),
                            "html");
                });
    }

    @SuppressWarnings("UnusedReturnValue")
    @Attachment(value = "Browser cookies", type = "text/plain")
    private static String collectCookies() {
        return getWebDriver().manage().getCookies().stream()
                .map(Cookie::toString)
                .collect(Collectors.joining("\n\n"));
    }

    private static List<LogEntry> getConsoleLogs() {
        return getWebDriver().manage().logs().get(LogType.BROWSER)
                .getAll()
                .stream()
                .filter(l -> !l.getLevel().equals(WARNING))
                .filter(l -> !l.getMessage().contains("chrome-extension"))
                .collect(Collectors.toList());
    }

    private static void runDiagnostic(Map.Entry<String, Runnable> diagnostic) {
        val diagnosticName = diagnostic.getKey();
        try {
            diagnostic.getValue().run();
            log.info(diagnosticName + " diagnostic: DONE");
        } catch (Exception e) {
            val errorMsg = diagnosticName + " diagnostic: ERROR - " + e.toString();
            log.error(errorMsg);
        }
    }
}
