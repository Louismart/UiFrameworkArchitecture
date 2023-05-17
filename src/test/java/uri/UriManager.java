package uri;

import com.codeborne.selenide.WebDriverRunner;
import com.google.common.base.Objects;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Stream;

public class UriManager {

    public static URI getOpenedUri() {
        return getOpenedUri(true, true);
    }

    public static URI getOpenedUri(boolean withQuery, boolean withFragment) {
        val openedUrlStr = WebDriverRunner.url();
        try {
            val uriBuilder = new URIBuilder(openedUrlStr);
            if (!withQuery) {
                uriBuilder.removeQuery();
            }
            if (!withFragment) {
                uriBuilder.setFragment(null);
            }
            // browsers remove the trailing slash if it's the only path in URI
            if (uriBuilder.getPath().equals("/")) {
                uriBuilder.setPath("");
            }
            return uriBuilder.build();
        } catch (URISyntaxException e) {
            throw new AssertionError(String.format("Browser is showing broken URI: %s", openedUrlStr));
        }
    }

    /**
     * Compares two URIs
     *
     * @param leftUri first URI to compare
     * @param rightUri second URI to compare
     * @param queryNeeded if true, query will be takes into account
     * @param fragmentNeeded if true, fragment will be taken into account
     * @return true if URIs match
     */
    public static boolean compareUris(URI leftUri, URI rightUri, boolean queryNeeded, boolean fragmentNeeded) {
        val sameScheme = Objects.equal(leftUri.getScheme(), rightUri.getScheme());
        val sameHost = Objects.equal(leftUri.getHost(), rightUri.getHost());
        val samePort = Objects.equal(leftUri.getPort(), rightUri.getPort());
        val sameQuery = Objects.equal(leftUri.getQuery(), rightUri.getQuery());
        val sameFragment = Objects.equal(leftUri.getFragment(), rightUri.getFragment());
        // The www.example.com and www.example.com/ are equivalent:
        // https://searchfacts.com/url-trailing-slash/#:~:text=The%20trailing%20slash%20on%20the%20root
        val samePath = Objects.equal(leftUri.getPath(), rightUri.getPath()) ||
                Stream.of(rightUri.getPath(), leftUri.getPath()).allMatch(UriManager::isPathEmpty);

        //noinspection SimplifiableConditionalExpression (this way is more human readable)
        return (sameScheme && sameHost && samePort && samePath &&
                (queryNeeded ? sameQuery : true) &&
                (fragmentNeeded ? sameFragment : true));
    }

    public static boolean compareUris(URI expectedUri, List<URI> uris, boolean queryNeeded, boolean fragmentNeeded) {
        return uris.stream().anyMatch(uri -> compareUris(uri, expectedUri, queryNeeded, fragmentNeeded));
    }

    @SneakyThrows
    public static URL buildUrl(String urlStr) {
        return new URL(urlStr);
    }

    private static boolean isPathEmpty(String path) {
        return path.equals("/") || path.equals("");
    }
}
