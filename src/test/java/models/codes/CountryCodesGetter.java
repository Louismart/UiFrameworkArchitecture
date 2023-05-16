package models.codes;

import lombok.val;

import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Pattern;

public class CountryCodesGetter {
    public static CountryCode getCountryCode(String codeOrCountryName) {
        CountryCode codeToReturn;
        if (codeOrCountryName.length() == 2) {
            codeToReturn = CountryCode.getByCodeIgnoreCase(codeOrCountryName);
        } else if (codeOrCountryName.length() == 0) {
            codeToReturn = null;
        } else {
            codeToReturn = CountryCode
                    .findByName(Pattern.compile("^" + codeOrCountryName + "$", Pattern.CASE_INSENSITIVE))
                    .stream()
                    .filter(code -> !code.getAssignment().equals(CountryCode.Assignment.NOT_IN_WORLDREMIT))
                    .collect(onlyOne(String.format("Country Code matching '%s'", codeOrCountryName)));
        }
        return Optional.ofNullable(codeToReturn)
                .orElseThrow(() -> new AssertionError(String.format("No CountryCode for: '%s'", codeOrCountryName)));
    }

    public static boolean isCountryValid(String countryName) {
        val fullMatch = Arrays.stream(CountryCode.values()).map(CountryCode::getName)
                .anyMatch(name -> name.equals(countryName));
        val noPrefixMatch = Arrays.stream(CountryCode.values()).map(CountryCode::getName)
                .map(CountryCodesGetter::stripPrefix)
                .anyMatch(name -> name.equals(countryName));
        return fullMatch || noPrefixMatch;
    }

    private static String stripPrefix(final String name) {
        return name.split(",")[0];
    }
}

