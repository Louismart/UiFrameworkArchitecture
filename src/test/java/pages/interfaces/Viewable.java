package pages.interfaces;

import configuration.EnvironmentConfig;

import static configuration.EnvironmentConfig.ENVIRONMENT_CONFIG;

public interface Viewable {

    default boolean isMobile() {
        return ENVIRONMENT_CONFIG.isMobile();
    }

    default boolean isDesktop() {
        return EnvironmentConfig.isDesktop();
    }
}
