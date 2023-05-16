package pages.components;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.cssClass;
import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;
import static java.lang.String.format;
import static models.codes.CountryCodesGetter.getCountryCode;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.google.common.collect.Streams;
import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.val;
import models.codes.CountryCode;
import models.codes.TransactionLanguage;
import org.openqa.selenium.Keys;
import pages.interfaces.HeaderInterface;

/**
 *
 * Header for all CMS pages
 */
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Header implements HeaderInterface {

    static String AUTH_ATTR_NAME = "data-is-authenticated";
    HeaderSelect sendFromCountrySelect = new HeaderSelect("country-selector");
    HeaderSelect languageSelect = new HeaderSelect("language-selector");
    SelenideElement liveUpdates = $("#live-updates, ul a[href$='live-updates']");
    SelenideElement hamburgerMenuIcon = $("#header-mobile-menu");
    SelenideElement hamburgerMenuDropdown = $("#header-links ul").parent();
    SelenideElement mainHeader = $(String.format("[%s]", AUTH_ATTR_NAME)).closest("header");
    SelenideElement myAccountMenuOrIcon = $("#header-user-account");
    SelenideElement helpLink = $("#help, ul a[href$='faq']");
    SelenideElement howItWorksLink = $("#how-it-works, ul a[href$='how-it-works']");
    SelenideElement referAFriendLink = $("#refer-a-friend, ul a[href$='refer-a-friend']");
    SelenideElement loginLink = $("#login, ul a[href*='account/login']");
    SelenideElement logoutLink = $("a[href*='account/logout']");
    SelenideElement signUpButton = $("#sign-up, ul a[href*='account/signup']");
    SelenideElement myAccountMenuDropdown = $("#header-user-account-menu ul").parent();
    SelenideElement mainLogo = $("header #worldremit_header_logo img");
    SelenideElement mainLogoLink = mainLogo.closest("a").as("Main logo link");
    SelenideElement authenticationFlag = $(String.format("header div[%s]", AUTH_ATTR_NAME));
    Runnable loadedCallback = this::waitUntilHeaderLoaded;

    @Override
    @Step("Select {0} as Send From in header")
    public void selectSendFromCountry(CountryCode countryCode) {
        if (isLoggedIn()) {
            throw new AssertionError("Cannot change Send From country for logged in users");
        }
        if (!sendFromCountrySelect.isDisplayed()) {
            expandHamburgerMenuIfNeeded();
        }
        sendFromCountrySelect.selectOptionByValue(countryCode.toString().toLowerCase());
        closeHamburgerMenuIfNeeded();
    }

    @Override
    public CountryCode getSelectedSendFromCountry() {
        waitUntilHeaderLoaded();
        if (!sendFromCountrySelect.isDisplayed()) {
            expandHamburgerMenuIfNeeded();
        }
        val countryValue = sendFromCountrySelect.getSelectedValue();
        closeHamburgerMenuIfNeeded();
        return getCountryCode(countryValue);
    }

    @Override
    public TransactionLanguage getSelectedLanguage() {
        waitUntilHeaderLoaded();
        if (!languageSelect.isDisplayed()) {
            expandHamburgerMenuIfNeeded();
        }
        val languageValue = languageSelect.getSelectedValue();
        closeHamburgerMenuIfNeeded();
        return TransactionLanguage.fromCode(languageValue);
    }

    @Override
    @Step("Select {0} language in header")
    public void selectLanguage(TransactionLanguage translationLanguage) {
        waitUntilHeaderLoaded();
        if (!languageSelect.isDisplayed()) {
            expandHamburgerMenuIfNeeded();
        }
        //on mobile, hamburger menu stays expanded after clicking language option that is already selected
        if (!languageSelect.getSelectedValue().equals(translationLanguage.getCode())) {
            languageSelect.selectOptionByValue(translationLanguage.getCode());
        } else {
            closeHamburgerMenuIfNeeded();
        }
        waitUntilHeaderLoaded();
    }

    @Override
    public void clickMainLogo() {
        mainLogo.click();
    }

    @Override
    public void clickHelp() {
        clickMenuItem(helpLink);
    }

    @Override
    public void clickHowItWorks() {
        clickMenuItem(howItWorksLink);
    }

    @Override
    public void clickLogin() {
        clickMenuItem(loginLink);
    }

    @Override
    public void clickLiveUpdates() {
        clickMenuItem(liveUpdates);
    }

    public void clickSignUp() {
        signUpButton.click();
    }

    @Override
    public SelenideElement getLoginLink() {
        if (!loginLink.isDisplayed()) {
            expandHamburgerMenuIfNeeded();
        }
        return loginLink;
    }

    @Override
    public SelenideElement getSendFromCountryDropdown() {
        if (!sendFromCountrySelect.isDisplayed()) {
            expandHamburgerMenuIfNeeded();
        }
        return sendFromCountrySelect.getSelectButton();
    }

    @Override
    public int getMainHeaderVerticalLocation() {
        return mainHeader.getLocation().getY();
    }

    @Override
    @Step("Check if user is logged in")
    public boolean isLoggedIn() {
        waitUntilHeaderLoaded();
        return authenticationFlag.has(attribute(AUTH_ATTR_NAME, "isAuthenticated"));
    }

    public SelenideElement getSelectLanguageDropdown() {
        waitUntilHeaderLoaded();
        if (!languageSelect.isDisplayed()) {
            expandHamburgerMenuIfNeeded();
        }
        return languageSelect.getSelectButton();
    }

    @Step("Click 'My Transactions' tab")
    public void clickMyTransactions() {
        val myTransactions = $(withText("My transactions"));
        if (myTransactions.isDisplayed()) {
            myTransactions.click();
        } else {
            clickMenuItem(myTransactions);
        }
    }

    @Step("Click 'My Account > Logout'")
    public void clickLogout() {
        clickMyAccountMenuItem(logoutLink);
    }

    public void clickReferAFriend() {
        clickMenuItem(referAFriendLink);
    }

    @Step("Expand \"My Account\" menu")
    public void expandMyAccountMenu() {
        myAccountMenuOrIcon.shouldBe(visible).click();
        waitForAnimationEndOf(myAccountMenuDropdown);
    }

    private void clickMenuItem(SelenideElement menuItem) {
        step(format("Click %s header item", menuItem.getSearchCriteria()), () -> {
            waitUntilHeaderLoaded();
            if (!menuItem.isDisplayed()) {
                expandHamburgerMenuIfNeeded();
            }
            menuItem.click();
        });
    }

    private void clickMyAccountMenuItem(final SelenideElement menuItem) {
        step(format("Click %s My Account menu item", menuItem.getSearchCriteria()), () -> {
            myAccountMenuOrIcon.click();
            waitForAnimationEndOf(myAccountMenuDropdown);
            menuItem.click();
        });
    }

    private void closeHamburgerMenuIfNeeded() {
        if (hamburgerMenuDropdown.isDisplayed()) {
            step("Close hamburger menu", () -> {
                hamburgerMenuDropdown.pressEscape();
                hamburgerMenuDropdown.shouldNotBe(visible);
            });
        }
    }

    private void expandHamburgerMenuIfNeeded() {
        waitUntilHeaderLoaded();
        if (hamburgerMenuIcon.isDisplayed() && !hamburgerMenuDropdown.isDisplayed()) {
            step("Expand hamburger menu", () -> {
                scrollToTop();
                hamburgerMenuIcon.click();
                hamburgerMenuDropdown.shouldBe(visible);
                waitForAnimationEndOf(hamburgerMenuDropdown);
            });
        }
    }

    private void waitUntilHeaderLoaded() {
        mainLogo.shouldBe(visible);
        authenticationFlag
                .shouldHave(attribute(AUTH_ATTR_NAME))
                .shouldNotHave(
                        attribute(AUTH_ATTR_NAME, "inProgress")
                                .because("Authentication API should reply in time"),
                        getLongTimeout());
    }

    /**
     * Represents a reusable dropdown component, used specifically in cms header.
     */
    private static class HeaderSelect implements Viewable {

        @Getter
        private final SelenideElement selectButton;
        private final SelenideElement optionsMenu;
        private final ElementsCollection options;

        /**
         * Creates a select component based on select's button id. Handles both desktop and mobile views automatically.
         * @param selectDesktopId data-testid of select's button element in desktop view, eg. 'country-selector'
         */
        public HeaderSelect(String selectDesktopId) {
            val selectId = isDesktop() ? selectDesktopId : format("%s-mobile", selectDesktopId);
            this.selectButton = $(format("[data-testid='%s']", selectId));
            this.optionsMenu = $(format("[role='presentation'][id='%s'] ul", selectId));
            this.options = optionsMenu.$$("[role='menuitem']");
        }

        public void selectOptionByValue(String value) {
            expand();
            val optionToSelect = Streams.stream(options.asFixedIterable())
                    .filter(option -> value.equals(option.getDomAttribute("value")))
                    .collect(onlyOne("Menu option with value: " + value));
            optionToSelect.click();
            optionToSelect.should(disappear);
        }

        public String getSelectedValue() {
            expand();
            val selectedOptionValue = options.findBy(cssClass("Mui-selected")).getDomAttribute("value");
            collapse();
            return selectedOptionValue;
        }

        public boolean isDisplayed() {
            return selectButton.isDisplayed();
        }

        private void expand() {
            if (!isExpanded()) {
                selectButton.click();
                optionsMenu.shouldBe(visible);
                waitForAnimationEndOf(optionsMenu);
            }
        }

        private void collapse() {
            if (isExpanded()) {
                optionsMenu.sendKeys(Keys.ESCAPE);
                optionsMenu.should(disappear);
            }
        }

        private boolean isExpanded() {
            return optionsMenu.isDisplayed();
        }
    }
}
