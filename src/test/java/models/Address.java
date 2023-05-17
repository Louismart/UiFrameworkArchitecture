package models;

import lombok.Builder;
import lombok.Value;
import models.codes.CountryCode;

@Value
@Builder
public class Address {

    @Builder.Default
    String buildingNameNo = "Kinsbourne Court 96-100";
    @Builder.Default
    String flatNo = "1";
    @Builder.Default
    String street = "Luton Road";
    @Builder.Default
    String suburb = "Test Suburb";
    @Builder.Default
    String city = "Harpenden";
    @Builder.Default
    String region = "Hertfordshire";
    @Builder.Default
    String postcode = "AL5 3BL";
    @Builder.Default
    CountryCode country = CountryCode.GB;

    public static Address validFromUs() {
        return Address.builder()
                .flatNo("Apt 1009")
                .buildingNameNo("701")
                .street("Pennsylvania Ave NW")
                .city("Washington")
                .region("DC")
                .postcode("20004")
                .country(CountryCode.US)
                .build();
    }

    public static Address invalidFromUs() {
        return Address.builder()
                .buildingNameNo("1500")
                .street("Pennsylvania Avenue")
                .city("Washington")
                .region("DC")
                .postcode("12345")
                .country(CountryCode.US)
                .build();
    }

    public static Address of(Sender sender) {
        return Address.builder()
                .flatNo(sender.getFlatUnit())
                .buildingNameNo(sender.getBuilding())
                .street(sender.getStreet())
                .suburb(sender.getSuburb())
                .city(sender.getCity())
                .region(sender.getRegion())
                .postcode(sender.getPostalCode())
                .country(sender.getCountry())
                .build();
    }
}
