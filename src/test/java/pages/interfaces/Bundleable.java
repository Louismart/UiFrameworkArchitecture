package pages.interfaces;

import lombok.val;

import java.util.Optional;
import java.util.ResourceBundle;

import static configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;

public interface Bundleable extends Translatable {

    default ResourceBundle getStrings() {
        val bundlePath = String.format("i18n/bundleables/%1$s/%1$s", this.getClass().getSimpleName());
        val language = Optional
                .ofNullable(this.getPageLanguage())
                .orElse(ENVIRONMENT_CONFIG.getLanguage());
        return ResourceBundle.getBundle(bundlePath, language.getLocale());
    }
}
