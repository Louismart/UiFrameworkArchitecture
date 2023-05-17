package creators;

import lombok.val;
import models.codes.CountryCode;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.function.Function;

import static java.util.Map.entry;

public class MobileNumberCreator {

    private static final Map<Integer, Pair<Long, Long>> mobileNumberRangesMap =
            Map.of(9, Pair.of(100_000_000L, 900_000_000L),
                    10, Pair.of(1_000_000_000L, 9_000_000_000L));

    private static final Function<CountryCode, Integer> countryToMobileNumberLengthMapper = country ->
            Map.ofEntries(
                    entry(CountryCode.US, 10),
                    entry(CountryCode.GB, 10)
            ).getOrDefault(country, 9);

    public static String generateUniqueMobileNumber(CountryCode countryCode) {
        val mobileNumberLength = countryToMobileNumberLengthMapper.apply(countryCode);
        val range = mobileNumberRangesMap.get(mobileNumberLength);
        return String.valueOf(range.getLeft() + (long) (Math.random() * (range.getRight() - range.getLeft())));
    }

    public static String createMaskedNumber(String unMaskedMobileNumber) {
        val mobileNumberParts = unMaskedMobileNumber.split(" ");
        if (mobileNumberParts.length == 2) {
            return mobileNumberParts[0] + mask(mobileNumberParts[1]);
        }

        return mask(mobileNumberParts[0]);
    }

    private static String mask(String mobileNumber) {
        return mobileNumber.replaceAll("(?!^)(.)(?!$)", "*");
    }
}
