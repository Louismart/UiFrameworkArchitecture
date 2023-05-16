package models.codes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Locale;

import static framework.collectors.WrCollectors.onlyOne;

/**
 * This is simplified enum, used instead of overcomplicated mix of LanguageCode and LocaleCode
 *
 */
@Getter
@RequiredArgsConstructor
@JsonDeserialize
public enum TransactionLanguage {
        DANISH("da", Locale.forLanguageTag("da-DA"), "Dansk"),
        DUTCH("nl", Locale.forLanguageTag("nl-NL"), "Nederlands"),
        ENGLISH("en", Locale.UK, "English"),
        FRENCH("fr", Locale.FRANCE, "Français"),
        GERMAN("de", Locale.GERMANY, "Deutsch"),
        SPANISH("es", Locale.forLanguageTag("es-ES"), "Español");

        private final String code;
        private final Locale locale;
        private final String nativeName;

        public String getFullName() {
            return this.toString().toLowerCase();
        }

        public static TransactionLanguage fromName(String languageStr) {
            return Arrays.stream(TransactionLanguage.values())
                    .filter(value -> value.getFullName().equals(languageStr))
                    .collect(onlyOne(String.format("TranslationLanguage enum of '%s'", languageStr)));
        }

        public static TransactionLanguage fromCode(String languageCode) {
            return Arrays.stream(TransactionLanguage.values())
                    .filter(value -> value.getCode().equals(languageCode))
                    .collect(onlyOne(String.format("TranslationLanguage enum for '%s'", languageCode)));
        }

        public static TransactionLanguage find(String codeOrName) {
            return Arrays.stream(TransactionLanguage.values())
                    .filter(value -> value.isMatching(codeOrName))
                    .collect(onlyOne(String.format("TranslationLanguage enum for '%s'", codeOrName)));
        }

        private boolean isMatching(String codeOrName) {
            return getCode().equalsIgnoreCase(codeOrName) ||
                    getFullName().equalsIgnoreCase(codeOrName);
        }
}
