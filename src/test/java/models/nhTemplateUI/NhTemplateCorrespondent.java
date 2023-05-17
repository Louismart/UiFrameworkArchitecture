package models.nhTemplateUI;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public enum NhTemplateCorrespondent {

    ACCESS_BANK_CSH("Access Bank - CSH", "891");

    String name;
    String value;
}
