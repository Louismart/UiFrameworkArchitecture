package pages.interfaces;

import com.codeborne.selenide.SelenideElement;
import com.worldremit.test.web.models.TranslationLanguage;
import com.worldremit.test.web.models.codes.CountryCode;
import com.worldremit.test.web.pages.interfaces.Loadable;
import models.codes.CountryCode;
import models.codes.TransactionLanguage;

public interface HeaderInterface extends Loadable {

    int getMainHeaderVerticalLocation();

    void selectSendFromCountry(CountryCode countryCode);

    TransactionLanguage getSelectedLanguage();

    CountryCode getSelectedSendFromCountry();

    void selectLanguage(TransactionLanguage translationLanguage);

    void clickMainLogo();

    void clickHelp();

    void clickHowItWorks();

    void clickLogin();

    void clickSignUp();

    void clickLiveUpdates();

    SelenideElement getSignUpButton();

    SelenideElement getLoginLink();

    SelenideElement getSendFromCountryDropdown();

    boolean isLoggedIn();
}
