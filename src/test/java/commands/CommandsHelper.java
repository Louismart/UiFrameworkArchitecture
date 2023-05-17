package commands;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.impl.WebElementSource;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Proxy;
import java.util.Arrays;

import static framework.collectors.WrCollectors.onlyOne;

public class CommandsHelper {

    /**
     * Accessing ((SelenideElementProxy) ((Proxy) selenideElement).h).webElementSource
     * Useful for getting the "real" Selenide elements, designed to be used for custom command execution
     */
    @SneakyThrows
    public static WebElementSource getWebElementSource(SelenideElement selenideElement) {
        val selenideElementProxy = Proxy.getInvocationHandler(selenideElement);
        val webElementSourceField = Arrays.stream(selenideElementProxy.getClass().getDeclaredFields())
                .filter(f -> f.getName().equals("webElementSource"))
                .collect(onlyOne("webElementSource field"));

        webElementSourceField.setAccessible(true);
        try {
            return (WebElementSource) webElementSourceField.get(selenideElementProxy);
        } finally {
            webElementSourceField.setAccessible(false);
        }
    }
}
