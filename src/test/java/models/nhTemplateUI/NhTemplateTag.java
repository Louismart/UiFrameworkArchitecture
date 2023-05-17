package models.nhTemplateUI;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NhTemplateTag {

    RESPONSE_HANDLER("Response Handler"),
    HOLD("Hold"),
    OUTAGE("Outage"),
    AUTO_NOTIFICATION("Auto Notification"),
    CANCELLATION_SERVICE("Cancellation Service"),
    SENDER_CHANGE_REQUEST_SERVICE("Sender Change Request Service"),
    RECIPIENT_CHANGE_REQUEST_SERVICE("Recipient Change Request Service"),
    BULK_NOTIFICATION("Bulk Notification"),
    MANUAL_NOTIFICATION("Manual Notification"),
    TEST("TEST");

    @JsonValue
    private final String value;
}
