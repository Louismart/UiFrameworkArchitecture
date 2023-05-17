package models.nhTemplateUI;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum NhSearchTemplateFiltersOption {

    NAME("1"),
    CATEGORY("2"),
    LAST_UPDATE("3"),
    LAST_UPDATE_BY("4");

    private final String value;
}
