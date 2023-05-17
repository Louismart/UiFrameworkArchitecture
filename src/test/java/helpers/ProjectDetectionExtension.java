package helpers;

import com.codeborne.selenide.Configuration;
import exception.WrongFrameworkUsageException;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import tags.E2eTest;
import tags.ProjectTest;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;
import static framework.collectors.WrCollectors.onlyOne;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * Checks what projects are involved in given test. The detections is done via
 * the ProjectTest-type annotations: CmsTest, PlutusTest, etc.
 *
 * Sets baseUrl based on the @Tag present in the above annotations. If there are multiple ones, the 'e2e' project is used.
 */
@Slf4j
public class ProjectDetectionExtension implements BeforeAllCallback, BeforeEachCallback {

    @Override
    public void beforeAll(final ExtensionContext context) {
        Configuration.baseUrl = getBaseUrlFrom(context);
    }

    @Override
    public void beforeEach(final ExtensionContext context) {
        Configuration.baseUrl = getBaseUrlFrom(context);
        Allure.addAttachment("project (baseUrl)", String.format("%s (%s)", getProjectNameFrom(context), Configuration.baseUrl));
    }

    /**
     * Looks for any test annotation that itself is annotated with `@ProjectTest`
     * If it finds one and only one, it returns the value of its `@Tag` annotation (i.e. the project name).
     * If there are multiple ones, it forces the test to be explicitly annotated with `@E2eTest` and returns `e2e`.
     */
    public static String getProjectNameFrom(final ExtensionContext context) {
        val mainProjectTestAnnotation = getMainProjectTestAnnotation(context);

        return getProjectNameFrom(mainProjectTestAnnotation.annotationType());
    }

    public static String getProjectNameFrom(final Class<? extends Annotation> mainProjectTestAnnotation) {
        // check for 0 or multiple @Tag is split into two, because multiple @Tag annotation become one @Tags annotation
        if (Arrays.stream(mainProjectTestAnnotation.getAnnotations())
                .anyMatch(annotation -> annotation instanceof Tags)) {
            throw new WrongFrameworkUsageException(mainProjectTestAnnotation + " must contain only one @Tag");
        }

        val tagAnnotation = (Tag) Arrays.stream(mainProjectTestAnnotation.getAnnotations())
                .filter(annotation -> annotation instanceof Tag)
                .collect(onlyOne("@Tag on " + mainProjectTestAnnotation));

        return tagAnnotation.value();
    }

    /**
     * Looks for any test annotation that itself is annotated with `@ProjectTest`
     * If it finds one and only one, it returns it.
     * If there are multiple ones, it forces the test to be explicitly annotated with `@E2eTest` and returns it.
     */
    public static Annotation getMainProjectTestAnnotation(final ExtensionContext context) {
        return getMainProjectTestAnnotation(getAllProjectTestAnnotations(context), getTestNameFrom(context));
    }

    public static List<Annotation> getAllProjectTestAnnotations(ExtensionContext context) {
        val allAnnotations = getAnnotationsFor(context);

        return allAnnotations.stream()
                .filter(annotation -> Arrays.stream(annotation.annotationType().getAnnotations())
                        .anyMatch(parentAnnotation -> parentAnnotation instanceof ProjectTest))
                .collect(toList());
    }

    private static Annotation getMainProjectTestAnnotation(final List<Annotation> annotations, String testName) {
        if (annotations.isEmpty()) {
            throw new WrongFrameworkUsageException(testName + " is broken: missing ProjectTest-type annotation");
        } else if (annotations.size() == 1) {
            // returning ProjectTest-type annotation
            return annotations.get(0);
        } else {
            // multiple ProjectTest-type annotations, let's validate that E2eTest exists and return it
            return annotations.stream()
                    .filter(annotation -> annotation instanceof E2eTest)
                    .collect(collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (list.size() == 1) { return list.get(0); }
                                throw new WrongFrameworkUsageException(
                                        testName + " is broken: Tests with multiple ProjectTest-type annotations must be explicitly annotated with @E2eTest");
                            }));
        }
    }

    private static String getBaseUrlFrom(final ExtensionContext context) {
        val projectName = Optional.ofNullable(getProjectNameFrom(context))
                .orElseThrow(() -> new WrongFrameworkUsageException("The test must be annotation with one of the ProjectTest "
                        + "tags of com.worldremit.test.web.tags.projects"));
        return Optional.ofNullable(ENVIRONMENT_CONFIG.getBaseUrl(projectName))
                .orElseThrow(() -> new WrongFrameworkUsageException(String.format("The project URL for '%s' is not present in "
                        + "the configuration", projectName)));
    }
    /**
     * Gets the current name of test from ExtensionContext.
     * @return name of test method if available, otherwise name of test class
     */
    public static String getTestNameFrom(ExtensionContext context) {
        return context.getTestMethod().map(Method::getName).orElse(context.getRequiredTestClass().getName());
    }
    /**
     * Gets all annotations for given given context - both on class and on method level.
     * @param context test context
     * @return List of all annotations of test
     */
    public static List<Annotation> getAnnotationsFor(ExtensionContext context) {
        val methodAnnotations = context.getTestMethod().map(AccessibleObject::getAnnotations).orElseGet(ArrayUtils::toArray);
        val classAnnotations = context.getRequiredTestClass().getAnnotations();
        return Arrays.asList(ArrayUtils.addAll(methodAnnotations, classAnnotations));
    }
}
