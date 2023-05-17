package models.nhTemplateUI;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import pages.nhTemplateUI.NhTemplatePage;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static configuration.EnvironmentConfig.getLongTimeout;

@SuperBuilder(toBuilder = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NhTemplateLoginPage extends NhTemplatePage {

    String uriPath = "";
    SelenideElement loginButton = $("[href*='login']");
    SelenideElement mainContentBlock = $("div.content.px-4");
    SelenideElement heading = mainContentBlock.$("h1");
    ElementsCollection authorizationErrorMsgs = mainContentBlock.$$("p");

    Runnable loadedCallback = () -> loginButton.shouldBe(visible, getLongTimeout());

    @Step("Click login link")
    public void clickLoginLink() {
        loginButton.shouldBe(visible, getLongTimeout()).click();
    }
}
