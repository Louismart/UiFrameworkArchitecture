package services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.val;
import models.codes.CountryCode;
import models.codes.CountryCodesGetter;
import models.codes.TranslationLanguage;
import org.apache.kafka.clients.producer.internals.Sender;


@Value
@Builder
public class UserAccount {
    int id;
    TranslationLanguage language;
    CountryCode country;
    @NonNull
    String email;
    Sender sender;
    RafEligibility rafEligibility;
    IntercomIdentity intercomIdentity;

    @Builder
    @Value
    public static class RafEligibility {

        boolean canReferFriend;
        boolean isSendingFromSupportedCountry;
    }

    @Builder
    @Value
    static class IntercomIdentity {

        int legacyUserId;
        String externalUserId;
        String externalUserIdHash;
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class UserAccountBuilder {

        /**
         * The UserAccount API response has the country code nested as:
         *     "country": {
         *       "code": "gb"
         *     },
         * @param countryJson JsonNode of UserAccount response
         * @return builder
         */
        @JsonProperty("country")
        public UserAccount.UserAccountBuilder country(JsonNode countryJson) {
            val countryCodeStr = countryJson.get("code").asText();
            return country(countryCodeStr);
        }

        public UserAccount.UserAccountBuilder country(String countryStr) {
            val countryCode = CountryCodesGetter.getCountryCode(countryStr);
            return country(countryCode);
        }

        public UserAccount.UserAccountBuilder country(CountryCode countryCode) {
            this.country = countryCode;
            return this;
        }
    }
}
