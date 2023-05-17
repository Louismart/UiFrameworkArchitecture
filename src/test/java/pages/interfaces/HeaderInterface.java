package pages.interfaces;
import com.codeborne.selenide.SelenideElement;
import models.codes.CountryCode;
import models.codes.TranslationLanguage;

public interface HeaderInterface extends Loadable {

    int getMainHeaderVerticalLocation();

    void selectSendFromCountry(CountryCode countryCode);

    TranslationLanguage getSelectedLanguage();

    CountryCode getSelectedSendFromCountry();

    void selectLanguage(TranslationLanguage translationLanguage);

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
