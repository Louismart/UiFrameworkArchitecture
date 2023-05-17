package srings;

import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;

public class StringManipulationManager {

    public static final String REGEXP_SPECIAL_CHARS = "[\\<\\(\\[\\{\\\\\\^\\-\\=\\$\\!\\|\\]\\}\\)\\?\\*\\+\\.\\>]";

    /**
     * Returns joined, human readable list of toString results using different separator for last item
     * E.g.:
     *  formatAsHrList(List.of("foo", "bar", "baz"), ", ", "and")
     *  -->
     *  "foo, bar and baz"
     * @param list List to join
     * @param separator separator to use for all but last element
     * @param lastItemSeparator separator to use for the last element
     * @return joined list of strings
     */
    public static <T> String formatAsHumanReadableList(List<T> list, String separator, String lastItemSeparator) {
        val paddedLastItemSeparator = " " + lastItemSeparator + " ";
        switch (list.size()) {
            case 0:
                return "";
            case 1:
                return list.get(0).toString();
            case 2:
                return StringUtils.join(list, paddedLastItemSeparator);
            default:
                val listWithoutLastItem = list.subList(0, list.size() - 1);
                val lastItem = list.get(list.size() - 1);
                val joinedListWithoutLastItem = StringUtils.join(listWithoutLastItem, separator);
                return StringUtils.joinWith(paddedLastItemSeparator, joinedListWithoutLastItem, lastItem);
        }
    }

    public static <T> String formatAsHumanReadableList(List<T> list, String lastItemSeparator) {
        return formatAsHumanReadableList(list, ", ", lastItemSeparator);
    }

    /**
     * Returns modified string, to be used as slug in URLs
     *
     * The same logic for slugify method is implemented in CMS project
     *
     * @param name Value to slugify
     * @param toLowerCase If true, the output String will be lower-case
     * @return slugified String (i.e. URL-friendly)
     */
    public static String slugify(String name, boolean toLowerCase) {
        val nameWithProperCase = toLowerCase ? name.toLowerCase() : name;
        return nameWithProperCase
                .replaceAll("\\s", "-")         // Replace spaces with -
                .replaceAll("[^\\w\\-]", "")    // Remove all non-word chars
                .replaceAll("--+", "-")         // Replace multiple - with single -
                .replaceAll("^-+", "")          // Trim - from start of text
                .replaceAll("-+$", "");         // Trim - from end of text
    }

    /**
     * Returns modified string, to be used as an URL-friendly name in PascalCase
     *
     * @param name Value to convert to PascalCase
     * @return PascalCasified String (i.e. Human-readable)
     */
    public static String pascalCasify(String name) {
        return Arrays.stream(slugify(name, false).split("-"))
                .map(StringUtils::capitalize)
                .collect(joining());
    }

    /**
     * Returns joined non-blank strings with the "space" delimiter
     *
     * @param strings variable to joining
     * @return joined string with "space" delimiter
     */
    public static String joinNonBlankStrings(String... strings) {
        return Stream.of(strings)
                .filter(org.junit.platform.commons.util.StringUtils::isNotBlank)
                .collect(joining(" "));
    }
}
