package models.nhTemplateUI;

import com.codeborne.selenide.SelenideElement;
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
public class NhTemplateStartPage extends NhTemplatePage {
    String uriPath = "";
    SelenideElement logOutButton = $("#logout");
    SelenideElement formBlock = $("div.form-row");
    SelenideElement filterByField = formBlock.$(".custom-select");
    SelenideElement searchField = formBlock.$(".form-control");

    Runnable loadedCallback = () -> logOutButton.shouldBe(visible, getLongTimeout());

}
