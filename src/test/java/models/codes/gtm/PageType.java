package models.codes.gtm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum PageType {
    // source: https://worldremit.atlassian.net/browse/WR-531
    HOMEPAGE("homepage"),
    PROMO_MESSAGE("promo message"),
    COUNTRY("country"),
    COUNTRY_SERVICE("country service"),
    COUNTRY_OPERATOR("country operator"),
    TRANSFER_PROCESS("transfer process"),
    MY_ACCOUNT("my account"),
    ACCOUNT("account"),
    FAQ("faq"),
    NEWS("news"),
    ERROR_PAGE("error page"),
    COMPANY("company"),
    WEBSITE("website"),
    BRAND("brand"),
    BLOG("blog"),
    INSTANT_WIN("instant win"),
    LOGIN("login"),
    SIGN_UP("signup"),
    RECIPIENTS("recipients"),
    RECIPIENT_PAGES("recipient pages"),
    MY_SETTINGS("/en/payments/mysettings"), // TODO: fix the settings pageType after migration to new GTM container
    IDENTITY("identity");

    final String value;

    public static PageType find(String pageTypeStr) {
        return Arrays.stream(PageType.values())
                .filter(value -> value.getValue().equals(pageTypeStr))
                .collect(onlyOne(String.format("PageType enum for '%s'", pageTypeStr)));
    }
}
