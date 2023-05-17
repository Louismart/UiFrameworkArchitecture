package configuration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;

import java.net.URL;
import java.util.Objects;

import static configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;

@RequiredArgsConstructor
public enum WebDriverType {
    // Uses native browser, but in headless mode. Should be avoided, because behavior is different than NATIVE
    HEADLESS(null, null, null),
    // Uses native browser, useful for test development
    NATIVE(null, null, null),
    // Uses local Selenoid container, useful for running tests locally
    SELENOID("http://localhost:4444/wd/hub", null, null),
    // Uses WR cloud-based Moon, to be used in pipelines
    MOON("https://ourentity.cloud.aerokube.com/wd/hub", ENVIRONMENT_CONFIG.getMoonUser(), ENVIRONMENT_CONFIG.getMoonPassword());
    @Getter
    private final String remoteUrl;
    @Getter
    private final String userName;
    @Getter
    private final String password;

    public final boolean isRemote() {
        return this.getRemoteUrl() != null;
    }

    @SneakyThrows
    public final String getAuthenticatedRemoteUrl() {
        return new URIBuilder(getRemoteUrl()).setUserInfo(getUserName(), getPassword()).build().toURL().toExternalForm();
    }

    @SneakyThrows
    public final URL getRemoteBaseUrl() {
        if (Objects.isNull(remoteUrl)) {
            return null;
        }
        return new URIBuilder(getRemoteUrl()).setPath("").setUserInfo(getUserName(), getPassword()).build().toURL();
    }
}
