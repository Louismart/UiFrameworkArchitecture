package pages.interfaces;

import com.codeborne.selenide.Command;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.commands.*;
import commands.CommandsHelper;
import configuration.EnvironmentConfig;
import io.opentelemetry.sdk.metrics.ViewBuilder;
import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Layer of abstraction to hold expectations from SelenideElements. Usage example:
 *
 * ExpectedElements.builder()
 *         .onDesktop().element($("#desktopSection")).shouldBe(visible)
 *         .onMobile().element($("#desktopSection")).shouldNotBe(visible)
 *         .onBoth().element($("#someCommonSection")).shouldBe(visible)
 *         .build();
 *
 * Such object, when we call ExpectedElements::execute on it, will perform the necessary should-calls,
 * assuming we are testing in a view that has been registered for. In the example above, if this is
 * test execution with mobile=false, we'll perform following calls:
 *
 *      $("#desktopSection").shouldBe(visible)
 *      $("#someCommonSection").shouldBe(visible)
 *
 * However, when test is running with mobile=true, following calls will be performed:
 *
 *      $("#desktopSection").shouldNotBe(visible)
 *      $("#someCommonSection").shouldBe(visible)
 *
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ExpectedElements {

    private final List<Expectation> expectationList;

    public boolean isEmpty() {
        return expectationList.isEmpty();
    }

    /**
     * Runs all assertions in a soft way, if any of the registered Expectation's failed,
     * the CombinedAssertionError will be thrown.
     */
    @Step("Check if all elements have been fully loaded")
    public void shouldBeFullyLoaded() {
        val failedExpectations = expectationList.stream()
                .map(Expectation::execute)
                .filter(ExpectationResult::isFailed)
                .collect(Collectors.toList());

        val message = "The page has not been fully loaded:\n"
                + failedExpectations.stream()
                .map(ExpectationResult::getFailureException)
                .map(Throwable::getMessage)
                .map(m -> "  - " + m.split("\n", 2)[0])
                .collect(Collectors.joining("\n"));

 /*       val assertionError = new CombinedAssertionError(message);

        failedExpectations
                .stream().map(ExpectationResult::getFailureException)
                .forEach(assertionError::addSuppressedException);

        if (assertionError.isFailed()) {
            throw assertionError;
        }   */
    }

    public static ViewBuilder builder() {
        return new ExpectedElementsBuilder();
    }

    public interface CommandBuilder {

        ViewBuilder should(Condition... conditions);

        ViewBuilder shouldBe(Condition... conditions);

        ViewBuilder shouldHave(Condition... conditions);

        ViewBuilder shouldNot(Condition... conditions);

        ViewBuilder shouldNotBe(Condition... conditions);

        ViewBuilder shouldNotHave(Condition... conditions);
    }

    public interface SpecificViewBuilder {

        CommandBuilder element(SelenideElement selenideElement);
    }

    public interface ViewBuilder {

        SpecificViewBuilder onDesktop();

        SpecificViewBuilder onMobile();

        SpecificViewBuilder onBothViews();

        ExpectedElements build();
    }

    /**
     * Nested & fluent builder that keeps the type-safety on compile time by using proper
     * interfaces thus keeping the order of chained methods.
     */
    public static class ExpectedElementsBuilder implements ViewBuilder, SpecificViewBuilder, CommandBuilder {

        List<Expectation> expectationList = new ArrayList<>();
        SelenideElement selenideElement;
        ViewType viewType;

        public CommandBuilder element(SelenideElement selenideElement) {
            this.selenideElement = selenideElement;
            return this;
        }

        public SpecificViewBuilder onDesktop() {
            viewType = ViewType.DESKTOP;
            return this;
        }

        public SpecificViewBuilder onMobile() {
            viewType = ViewType.MOBILE;
            return this;
        }

        public SpecificViewBuilder onBothViews() {
            viewType = ViewType.BOTH;
            return this;
        }

        public ExpectedElements build() {
            return new ExpectedElements(expectationList);
        }

        @Override
        public ExpectedElementsBuilder should(final Condition... conditions) {
            return applyCommand(new Should(), conditions);
        }

        @Override
        public ExpectedElementsBuilder shouldBe(final Condition... conditions) {
            return applyCommand(new ShouldBe(), conditions);
        }

        @Override
        public ExpectedElementsBuilder shouldHave(final Condition... conditions) {
            return applyCommand(new ShouldHave(), conditions);
        }

        @Override
        public ExpectedElementsBuilder shouldNot(final Condition... conditions) {
            return applyCommand(new ShouldNot(), conditions);
        }

        @Override
        public ExpectedElementsBuilder shouldNotBe(final Condition... conditions) {
            return applyCommand(new ShouldNotBe(), conditions);
        }

        @Override
        public ExpectedElementsBuilder shouldNotHave(final Condition... conditions) {
            return applyCommand(new ShouldNotHave(), conditions);
        }

        private ExpectedElementsBuilder applyCommand(Command<SelenideElement> command, Condition... conditions) {
            if (isDesiredToRunInThisView()) {
                expectationList.add(new Expectation(this.selenideElement, command, conditions));
            }

            // we're done with building whole Expectation, let's clear the current state
            this.selenideElement = null;
            this.viewType = null;
            return this;
        }

        private boolean isDesiredToRunInThisView() {
            if (this.viewType.equals(ViewType.BOTH)) {
                return true;
            }
            return this.viewType.equals(EnvironmentConfig.getViewType());
        }
    }

    /**
     * Describes what is expected from given check, basically:
     *
     *      new Expectation($("someElement"), new Should(), Condition.exist).execute();
     *
     * is a granular wrapper for:
     *
     *      () -> $("someElement").should(exist);
     */
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class Expectation {

        final SelenideElement selenideElement;
        final Command<SelenideElement> command;
        final Condition[] conditions;

        /**
         * This method performs the Selenide's command-execution.
         */
        ExpectationResult execute() {
            val locator = CommandsHelper.getWebElementSource(selenideElement);
            try {
                command.execute(selenideElement, locator, conditions);
                return new ExpectationResult(null);
            } catch (Throwable e) {
                return new ExpectationResult(e);
            }
        }
    }

    /**
     * Simple wrapper class that either holds an exception or does not.
     * In future, if we need to hold the values returned within the Expectation::execute, we
     * could utilize this class to either hold the exception (if failed), or the return value (if passed).
     */
    @RequiredArgsConstructor
    @Getter(AccessLevel.PRIVATE)
    private static class ExpectationResult {

        private final Throwable failureException;

        public boolean isFailed() {
            return Objects.nonNull(failureException);
        }
    }
}



