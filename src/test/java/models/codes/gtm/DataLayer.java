package models.codes.gtm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.ToString;
import lombok.Value;
import models.codes.CountryCode;
import models.codes.TranslationLanguage;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
        /* DataLayer representation. DO NOT use primitive types, so the fields can be set to null.
        */
@Builder(toBuilder = true)
@Value
public class DataLayer {
            public static final String PAGE_LOADED = "page loaded";
            public static final String LOGGED_IN = "logged in";
            public static final String NOT_LOGGED_IN = "not logged in";
            private static final String NO_TRANSFERS_SIGNATURE = "no transfers";
            // General - must be set for ALL pages
            PageType pageType;
            String pageName;
            TranslationLanguage pageLanguage;
            CountryCode senderCountry;
            CountryCode senderCountryIso; // same as senderCountry
            String visitorStatus;
            // Logged In - must be set for ALL pages if user is logged in
            String age;
            String gender;
            String lastTransferDate;
            String numTransfers;
            String customerId;
            // For CLPs - must be set for ALL CountryLandingPage objects
            String tabInformation;
            String transferTime;
            String exchangeRate;
            String feesFrom;
      //      ServiceCode serviceType;
            CountryCode destinationCountry;
            CountryCode destinationCountryIso; // same as destinationCountry
            String countryCorridorIso; // populated from senderCountry and destinationCountry
            // For CLPs with send bands defined


            // User interactions
            String dropdownName;
            String dropdownValue;
            String event;
            String interactionName;
            String linkName;
            // For mobile apps banner
            String appName;
            String linkType;
            String linkLocation;
            //For calculator lite
            String liteCalcCorridor;
            String liteCalcCurrencyCorridor;
            String liteCalcSendOrReceive;
            String liteCalcFxRate;
            String liteCalcAmount;
            String liteCalcDropdownName;
            String liteCalcDropdownValue;
            String liteCalcMessage;
            String liteCalcServiceType;
            String liteCalcDropdownSearchValue;
            // For full calculator
            String calcMessage;
            String calcDropdownName;
            String calcDropdownValue;
            String amountField;
            String calculatorType;
            String calcDropdownSearchValue;
            // Payments Data Layer
            String requestDevice;
            String cardPaymentAction;

            //Plutus Data Layer
            String loginLocation;
            String fieldName;
            String formName;
            String formField;
            String formResult;
            String formMessage;
            String codeType;
            String transferCta;

            //For FAQ page
            String faqInteractionType;
            String faqInteractionValue;
            String faqInteractionSearchKeyword;

            //BPR Data Layer
            String dropDownName;
            String dropdownSelection;
            String recipientPagesCta;

            //Airtime Data Layer
            String phoneValid;

            //A/B Test Data Layer
            String eventAction;
            String eventCategory;
            String eventLabel;

            //KYC flow related
            String event_name;
            String event_action;
            String event_label;
            String interaction;
            String error_text;

            @ToString.Include(name = "pageType", rank = 2)
            private String pageTypeToString() {
                if (pageType == null) {
                    return "null";
                }
                return pageType.getValue();
            }

            @ToString.Include(name = "pageLanguage", rank = 1)
            private String pageLanguageToString() {
                if (pageLanguage == null) {
                    return "null";
                }
                return pageLanguage.getFullName();
            }

            @ToString.Include(name = "senderCountry")
            private String senderCountryToString() {
                if (senderCountry == null) {
                    return "null";
                }
                return senderCountry.getName().toLowerCase();
            }

            /**
             * Class to customize lombok-generated builder. We need this so test propagate UserAccount data easily
             */
            @JsonPOJOBuilder(withPrefix = "")
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class DataLayerBuilder {

                public DataLayerBuilder lastTransferDate(LocalDate localDate) {
                    return lastTransferDate(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
                }

                public DataLayerBuilder lastTransferDate(String lastTransferDate) {
                    this.lastTransferDate = lastTransferDate;
                    return this;
                }

            }


}
