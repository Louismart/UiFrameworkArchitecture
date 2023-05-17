package pages.nhTemplateUI;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import models.codes.TranslationLanguage;
import pages.BasePage;

import static configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;

/**
 * This class has been created for nh-template-ui portal
 *
 * which is used for managing templates and bulk notifications
 *
 * Only nh-template-ui pages should extend NhTemplatePage
 */
@SuperBuilder(toBuilder = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class NhTemplatePage extends BasePage {
    TranslationLanguage pageLanguage = null;  // no language code in NH URL
    @Builder.Default
    String baseUrl = ENVIRONMENT_CONFIG.getBaseUrl("nh-template-ui");
}
