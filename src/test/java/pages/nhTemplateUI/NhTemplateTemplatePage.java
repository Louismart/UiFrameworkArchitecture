package pages.nhTemplateUI;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import models.nhTemplateUI.NhSearchTemplateFiltersOption;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Selenide.$;
import static configuration.EnvironmentConfig.getLongTimeout;
import static waiters.AssertionWaiter.waitUntilCall;

@SuperBuilder(toBuilder = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NhTemplateTemplatePage extends NhTemplatePage {

    String uriPath = "/templates";
    SelenideElement mainContentBlock = $("div.content.px-4");
    SelenideElement heading = mainContentBlock.$$("h1").findBy(text("Templates"));
    ElementsCollection authorizationErrorMsgs = mainContentBlock.$$("p");
    SelenideElement formBlock = $("div.form-row");
    SelenideElement filterByField = formBlock.$(".custom-select");
    SelenideElement searchField = formBlock.$(".form-control");
    SelenideElement templatesTable = mainContentBlock.$(".table");
    @Getter
    Runnable loadedCallback = () -> {
        heading.shouldBe(visible, getLongTimeout());
        templatesTable.shouldBe(visible);
      //  waitUntilCall(() -> templatesTable::getSize, isStableFor(1800));
    };

    @Step("Search templates")
    public void searchTemplates(NhSearchTemplateFiltersOption option, String searchPhrase) {
        selectFilterBy(option);
        setSearchPhrase(searchPhrase);
    }

    @Step("Search for the phrase")
    private void setSearchPhrase(String searchPhrase) {
     //   searchField.execute(new ClearUsingBackspace());
        searchField.sendKeys(searchPhrase);
        searchField.shouldHave(value(searchPhrase));
    }

    @Step("Select filter option")
    private void selectFilterBy(NhSearchTemplateFiltersOption option) {
        smartSelectOptionByValue(filterByField, option.getValue());
        filterByField.getSelectedOption().shouldHave(attribute("value", option.getValue()));
    }

}
