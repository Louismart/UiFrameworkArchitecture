package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import models.codes.CountryCode;
import org.apache.commons.codec.digest.DigestUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import static creators.MobileNumberCreator.generateUniqueMobileNumber;
import static java.util.Map.entry;
import static models.codes.CountryCode.*;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang3.StringUtils.capitalize;

/**
 * Class for Sender deserialization and test usage.
 * <p>
 * Sender comes from backend as follows:
 * <p>
 * {
 * "SenderReferenceNumber" : "WR10453423",
 * "SenderFirstName" : "Sender First Name",
 * "SenderLastName" : "Sender Last Name",
 * "SenderDateOfBirth" : "1970-07-19T00:00:00",
 * "SenderGender" : "Male",
 * "SenderMobile" : "1234567890",
 * "SenderFlatUnit" : "3",
 * "SenderBuilding" : "23"
 * ...
 * }
 * <p>
 * For serialization (e.g. to create or activate a user), we use another class:
 *
 *
 */
@Value
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PUBLIC, makeFinal = true)
public class Sender {
    public static long ADMIN_USER_ID = 71L; // This is the ID number of the Admin role in DB
    static String DEFAULT_PASSWORD = "Pa$$w0rd12";
    static String DEFAULT_FIRST_NAME = "Leo";
    static String DEFAULT_LAST_NAME = "Tarred";
    static LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.of(1970, 7, 19);
    static String DEFAULT_GENDER = "MALE";
    static String DEFAULT_MOBILE_NO = "1234567890";
    static String DEFAULT_PHONE_NO = "9876543210";
    static String DEFAULT_OCCUPATION = "Unemployed";
    static String DEFAULT_INDIVIDUAL_NUMBER = "123456789012";
    static String DEFAULT_ID_TYPE = "PASSPORT";
    static Address DEFAULT_ADDRESS = Address.builder().build();
    static String DEFAULT_VALID_SA_ID_CARD_ANSWER = "NO";
    static CountryCode DEFAULT_COUNTRY_OF_BIRTH = GB;
    static CountryCode DEFAULT_COUNTRY = GB;
    private static Map<CountryCode, Supplier<Sender>> SENDER_COUNTRY_MAP = Map.ofEntries(
            entry(
                    CountryCode.AU,
                    () -> Sender.buildDefault().toBuilder()
                            .country(AU)
                            .region("ACT")
                            .nationality(AU.getAlpha2())
                            .documentNumber("AA123456")
                            .mobile(generateUniqueMobileNumber(AU))
                            .build()
            ),
            entry(
                    CountryCode.CA,
                    () -> Sender.buildDefault().toBuilder().country(CA).postalCode("A1B 2C3").occupation("123").region("AB").mobile(generateUniqueMobileNumber(CA)).build()
            ),
            entry(
                    CountryCode.MY,
                    () -> Sender.buildDefault().toBuilder()
                            .country(MY)
                            .middleName("tod")
                            .occupation("Unemployed")
                            .countryOfBirth(AT)
                            .idType(null)
                            .region("JHR")
                            .idType(null)
                            .mobile(generateUniqueMobileNumber(MY))
                            .build()
            ),
            entry(
                    CountryCode.RW,
                    () -> Sender.buildDefault().toBuilder()
                            .country(RW)
                            .middleName("tod")
                            .countryOfBirth(AT)
                            .idType(null)
                            .idType(null)
                            .idNumber("1234567")
                            .mobile(generateUniqueMobileNumber(RW))
                            .build()
            ),
            entry(
                    PL,
                    () -> Sender.buildDefault().toBuilder().country(PL).mobile(generateUniqueMobileNumber(PL)).build()
            ),
            entry(
                    CountryCode.US,
                    () -> Sender.buildDefault().toBuilder()
                            .country(US)
                            .region("DC")
                            .city("Washington")
                            .street("Pennsylvania Avenue")
                            .postalCode("20220-0001")
                            .state("WA")
                            .building("1500")
                            .countryOfBirth(CountryCode.US)
                            .socialSecurityNumber(new Faker().idNumber().ssnValid())
                            .mobile(generateUniqueMobileNumber(US))
                            .build()
            ),
            entry(
                    GB,
                    () -> Sender.buildDefault().toBuilder()
                            .country(GB)
                            .region("Hertfordshire")
                            .suburb("Test Suburb")
                            .mobile(generateUniqueMobileNumber(GB))
                            .build()
            ),
            entry(
                    BE,
                    () -> Sender.buildDefault().toBuilder()
                            .country(BE)
                            .countryOfBirth(BE)
                            .suburb("Test Suburb")
                            .mobile(generateUniqueMobileNumber(BE))
                            .build()
            ),
            entry(
                    CountryCode.ZA,
                    () -> Sender.buildDefault().toBuilder()
                            .country(ZA)
                            .building("African")
                            .mobile("123456789")
                            .region("EC")
                            .suburb("Some suburb")
                            .occupation("some occupation")
                            .zaPassportNumber("PAssport123")
                            .nationality("ZA")
                            .validSAIdCard("yes")
                            .zaIdNumber("8810121234567")
                            .mobile(generateUniqueMobileNumber(ZA))
                            .build()
            )
    );
    @JsonProperty("SenderReferenceNumber")
    String referenceNumber;
    @JsonProperty("SenderFirstName")
    String firstName;
    @JsonProperty("SenderLastName")
    String lastName;
    @JsonProperty("SenderMiddleName")
    String middleName;
    @JsonProperty("SenderEmail")
    String email;
    @JsonProperty("SenderPassword")
    String password;
    @JsonProperty("SenderReferralCode")
    String referralCode;
    @JsonProperty("SenderDateOfBirth")
    LocalDate dateOfBirth;
    @JsonProperty("SenderGender")
    String gender;
    @JsonProperty("SenderCountry")
    CountryCode country;
    @JsonProperty("SenderState")
    String state;
    @JsonProperty("SenderFlatUnit")
    String flatUnit;
    @JsonProperty("SenderBuilding")
    String building;
    @JsonProperty("SenderStreet")
    String street;
    @JsonProperty("SenderSuburb")
    String suburb;
    @JsonProperty("SenderCity")
    String city;
    @JsonProperty("SenderRegion")
    String region;
    @JsonProperty("SenderPostalCode")
    String postalCode;
    @JsonProperty("SenderMobile")
    String mobile;
    @JsonProperty("SenderPhoneNumber")
    String phone;
    @JsonProperty("SenderTermsAndConditionsCheckbox")
    Boolean termsAndConditions;
    @JsonProperty("SenderTermsAndConditionsCheckboxGDPR")
    Boolean termsAndConditionsGDPR;
    @JsonProperty("SenderOccupation")
    String occupation;
    @JsonProperty("SenderDocumentNumber")
    String documentNumber;
    @JsonProperty("SenderIDType")
    String idType;
    @JsonProperty("SenderIDNumber")
    String idNumber;
    @JsonProperty("SenderIdIssueDate")
    String idIssueDate;
    @JsonProperty("SenderIdExpireDate")
    String idExpireDate;
    @JsonProperty("SenderIdIssuingCountry")
    String idIssuingCountry;
    @JsonProperty("SenderZAPassportNumber")
    String zaPassportNumber;
    @JsonProperty("SenderZASouthAfricanIDCardNumberZACitizen")
    String zaIdNumber;
    @JsonProperty("SenderNationality")
    String nationality;
    @JsonProperty("SenderZAValidSouthAfricanIdCard")
    String validSAIdCard;
    @JsonProperty("SenderCountryOfBirth")
    CountryCode countryOfBirth;
    @JsonProperty("SocialSecurityNumber")
    String socialSecurityNumber;

    public String getAuthenticationToken() {
        return getAuthenticationToken(email, password);
    }

    public static String getAuthenticationToken(String email, String password) {
        return Base64.getEncoder().encodeToString(String.join(":", email, DigestUtils.md5Hex(password)).getBytes());
    }

    /**
     * @return Sender instance with minimum details to sign up
     */
    public static Sender buildBasic() {
        return Sender.builder()
                .country(DEFAULT_ADDRESS.getCountry())
                .email(generateUniqueEmail())
                .password(generateUniquePassword())
                .termsAndConditions(true)
                .build();
    }

    /**
     * @return Sender instance with minimum details to activate
     */
    public static Sender buildDefault() {
        return buildBasic().toBuilder()
                .firstName(generateRandomName())
                .middleName(generateRandomName())
                .lastName(generateRandomName())
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
                .countryOfBirth(DEFAULT_COUNTRY_OF_BIRTH)

                .mobile(generateUniqueMobileNumber(DEFAULT_COUNTRY))
                .phone(DEFAULT_PHONE_NO)
                .flatUnit(DEFAULT_ADDRESS.getFlatNo())
                .building(DEFAULT_ADDRESS.getBuildingNameNo())
                .street(DEFAULT_ADDRESS.getStreet())
                .city(DEFAULT_ADDRESS.getCity())
                .postalCode(DEFAULT_ADDRESS.getPostcode())

                .idNumber(DEFAULT_INDIVIDUAL_NUMBER)
                .occupation(DEFAULT_OCCUPATION)
                .build();
    }

    /**
     * @return Sender instance that already exists in DB in disabled state
     */
    public static Sender buildDisabled() {
        return Sender.buildBasic().toBuilder().email("test-qa+disabled@worldremit.com").password("Disabled1234!").build();
    }

    public static Sender fromUsWithoutCountryOfBirth() {
        return Sender.buildBasic()
                .toBuilder()
                .firstName(generateRandomName())
                .lastName(generateRandomName())
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
                .gender(DEFAULT_GENDER)
                .country(US)
                .region("DC")
                .city("Washington")
                .street("Pennsylvania Avenue")
                .postalCode("20220-0001")
                .state("WA")
                .building("1500")
                .socialSecurityNumber(new Faker().idNumber().ssnValid())
                .mobile(generateUniqueMobileNumber(US))
                .build();
    }

    public static Sender fromGbWithoutCountryOfBirth() {
        return buildBasic().toBuilder()
                .firstName(generateRandomName())
                .lastName(generateRandomName())
                .country(GB)
                .gender(DEFAULT_GENDER)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
                .mobile(generateUniqueMobileNumber(DEFAULT_COUNTRY))
                .street(DEFAULT_ADDRESS.getStreet())
                .city(DEFAULT_ADDRESS.getCity())
                .building("1500")
                .postalCode(DEFAULT_ADDRESS.getPostcode())
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging with one saved Credit Card (VISA)
     */
    public static Sender fromGbWithCreditCardSavedInLegacy() {
        return Sender.buildBasic().toBuilder()
                .country(GB)
                .email("save_card_legacy_test_gb@wrtest.example.com")
                .password("Pa$$w0rd12")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging with one saved Credit Card ({@link PaymentCard#buildCybPaymentCard()})
     */
    public static Sender fromGbWithCreditCardSavedInRewrite() {
        return Sender.buildBasic().toBuilder()
                .country(GB)
                .email("test-qa+gb_with_saved_card_rewrite2@worldremit.com")
                .password("Pa$$w0rd12")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging with one saved Credit Card (MASTERCARD)
     */
    public static Sender fromAuWithCreditCardSavedInRewrite() {
        return Sender.buildBasic().toBuilder()
                .country(CountryCode.AU)
                .email("save_card_rewrite_test_au@wrtest.example.com")
                .password("Pa$$w0rd12")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB both on staging and production
     */
    public static Sender existingFromAt() {
        return Sender.buildBasic().toBuilder()
                .country(CountryCode.AT)
                .email("test-qa+at10@worldremit.com")
                .password("Prime1230!*-")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging and prod
     */
    public static Sender existingFromCa() {
        return Sender.buildBasic().toBuilder()
                .country(CountryCode.CA)
                .email("test-qa+canada1@worldremit.com")
                .password("Pa$$w0rd12")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging
     */
    public static Sender existingFromGbWithCountryOfBirthSet() {
        return Sender.buildBasic().toBuilder()
                .country(GB)
                .email("test-qa+gb_with_country_of_birth_set@wr.com")
                .password("Pa$$w0rd12")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging and prod
     */
    public static Sender existingFromUk() {
        return Sender.builder().email("test-qa+gb@worldremit.com").password("7ES82?.ycTg4-n+").build();
    }

    /**
     * @return Sender instance that already exists in DB on staging
     */
    public static Sender existingFromRw() {
        return Sender.builder().email("rwaccount@example.com").password("Qwerty123!@#").mobile("+250 1234567891").country(RW).build();
    }

    /**
     * @return Sender instance that already exists in DB on staging
     */
    public static Sender existingFromMy() {
        return Sender.builder().email("myaccount@example.com").password("Qwerty123!@#").mobile("+60 1234567891").country(MY).build();
    }

    /**
     * @return Sender instance that already exists in DB on staging and prod
     */
    public static Sender existingFromUs() {
        return Sender.buildDefault()
                .toBuilder()
                .country(US).state("AL")
                .email("test-qa+us7@worldremit.com").password("l@lWU4FCQzyBGttyOnfX")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging and prod
     */
    public static Sender existingFromAu() {
        return Sender.buildDefault()
                .toBuilder()
                .country(AU).region("ACT")
                .email("test-qa+au3@worldremit.com").password("&&AQAZDiXeXpa7k")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging and prod
     */
    public static Sender existingFromSe() {
        return Sender.buildDefault()
                .toBuilder()
                .country(SE)
                .email("test-qa+se10@worldremit.com")
                .password("Prime3210+!-")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging and prod
     */
    public static Sender existingFromCy() {
        return Sender.buildDefault()
                .toBuilder()
                .country(CY)
                .email("test-qa+cy10@worldremit.com")
                .password("Fghyr8907$%!+")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB on staging
     */
    public static Sender existingWithOutage() {
        return Sender.builder().email("outageUser@wrtest.com").password("Qwerty123!@#").build();
    }

    /**
     * @return Sender instance that already exists in DB, should have 1 non completed (in progress) transaction
     */
    public static Sender existingWithOneTransaction() {
        return Sender.buildBasic().toBuilder()
                .email("gbcitizen1transaction@worldremit.example.com")
                .password("PKLdp4Jw6d5phFa")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB, should have 1 completed transaction
     */
    public static Sender existingWithOneSuccessfulTransaction() {
        return Sender.buildBasic().toBuilder()
                .email("gbcitizen1completed_tran@worldremit.example.com")
                .password("PKLdp4Jw6d5phFa")
                .referralCode("REFERDYAPD2W3X5YZ")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB, the user in "Under Review" status
     */
    public static Sender existingWithUnderReviewStatus() {
        return Sender.buildBasic().toBuilder()
                .email("tjwron0939@wr.pl")
                .password("Test12345")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB, the user in "Not approved" status
     */
    public static Sender existingWithNotApprovedStatus() {
        return Sender.buildBasic().toBuilder()
                .email("tjwron1524@wr.pl")
                .password("Test12345")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB, the user in "approved" status
     */
    public static Sender existingCompliantFromUsWithRecipients() {
        return Sender.builder()
                .country(US)
                .state("CO")
                .email("test-qa+us_compliant_with-recips@worldremit.com")
                .password("Pa$$w0rd12")
                .build();
    }

    /**
     * @return Sender instance that already exists in DB, the user in "approved" status
     */
    public static Sender existingCompliantFromGbWithRecipients() {
        return Sender.builder()
                .country(GB)
                .email("test-qa+gb_compliant_with-recips@worldremit.com")
                .password("Pa$$w0rd12")
                .build();
    }

    /**
     * @return US sender instance that already exists in staging DB with one saved Credit Card (VISA)
     */
    public static Sender existingFromUsWithVisaCard() {
        return Sender.builder()
                .email("test-qa+us_visa_card_tb@worldremit.com")
                .password("Pa$$w0rd12")
                .country(CountryCode.US)
                .region("DC")
                .city("Washington")
                .street("Pennsylvania Avenue")
                .postalCode("12345")
                .building("1500")
                .build();
    }

    /**
     * @return PL sender instance that already exists in staging DB
     */
    public static Sender existingFromPl() {
        return Sender.builder()
                .email("test-qa+poland@worldremit.com")
                .password("Pa$$w0rd12")
                .country(CountryCode.PL)
                .city("Warsaw")
                .street("Polska")
                .postalCode("50555")
                .building("1500")
                .build();
    }

    public static String generateUniqueEmail() {
        return String.format(
                "test-qa+%s_%s@worldremit.com",
                new SimpleDateFormat("yyyyMMdd.HHmmssSSS").format(new Date()),
                new Random().nextInt(9999));
    }

    public static String generateUniquePassword() {
        return String.format("1Az%s", new Faker().internet().password(9, 15, true, true, true));
    }

    public static Sender buildDefaultFrom(CountryCode senderCountry) {
        return SENDER_COUNTRY_MAP.getOrDefault(
                senderCountry,
                () -> Sender.buildDefault().toBuilder()
                        .country(senderCountry)
                        .mobile(generateUniqueMobileNumber(senderCountry))
                        .build()).get();
    }

    public static String generateRandomName() {
        return capitalize(randomAlphabetic(15).toLowerCase());
    }
}
