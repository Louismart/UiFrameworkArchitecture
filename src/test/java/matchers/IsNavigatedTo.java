package matchers;

import org.hamcrest.Matcher;
import pages.interfaces.Page;

import static matchers.IsLoaded.isLoaded;
import static matchers.IsOpened.isOpened;
import static org.hamcrest.Matchers.allOf;

public class IsNavigatedTo {

    public static Matcher<Page> isNavigatedTo() {
        return allOf(isOpened(), isLoaded());
    }

    public static Matcher<Page> withQueryParam() {
        return allOf(isOpened().setQueryNeeded(true), isLoaded());
    }
}
