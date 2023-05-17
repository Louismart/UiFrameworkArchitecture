package cookies;

import lombok.EqualsAndHashCode;
import org.openqa.selenium.Cookie;

import static cookies.CookieManager.forDomain;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class WrCookie {

    public String toString() {
        return String.format("%s(%s=%s)", this.getClass().getSimpleName(), getName(), getValue());
    }

    public boolean isSet() {
        return CookieManager.isCookieSet(this);
    }

    public boolean isSetFor(String urlToCheckCookieOn) {
        return forDomain(urlToCheckCookieOn).isCookieSet(this);
    }

    public Cookie toCookie() {
        return new Cookie(getName(), getValue());
    }

    @EqualsAndHashCode.Include
    public abstract String getName();

    @EqualsAndHashCode.Include
    public abstract String getValue();
}
