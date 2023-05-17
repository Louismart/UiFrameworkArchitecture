package models.nhTemplateUI.credentials;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NhTemplateUserCredentials implements OktaUserCredentials {

    NHTEMPLATE_TEST_USER("QA Test", "qa-test", "Pa$$w0rd12");

    private final String simpleName;
    private final String username;
    private final String password;
}
