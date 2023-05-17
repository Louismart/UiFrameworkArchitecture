package models.nhTemplateUI;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.val;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Builder(toBuilder = true)
@Value
public class NhTemplate {

    private static String DEFAULT_TEMPLATE_NAME = "Default_Template";

    String name;
    String description;
    @Singular
    List<NhTemplateTag> tags;
    Boolean useNoReply;
    String modifiedBy;
    String modifiedDate;

    public static NhTemplate buildBasic() {
        val dateObject = LocalDateTime.now().toString();
        return NhTemplate.builder()
                .name(String.format("%s %s %d", DEFAULT_TEMPLATE_NAME, dateObject, new Random().nextInt(9999)))
                .description("Any description")
                .tag(NhTemplateTag.OUTAGE)
                .modifiedBy("QA Test")
                .modifiedDate(dateObject)
                .useNoReply(false)
                .build();
    }

    public static NhTemplate buildDefault() {
        return buildBasic().toBuilder()
                .tag(NhTemplateTag.AUTO_NOTIFICATION)
                .tag(NhTemplateTag.BULK_NOTIFICATION)
                .tag(NhTemplateTag.CANCELLATION_SERVICE)
                .tag(NhTemplateTag.MANUAL_NOTIFICATION)
                .tag(NhTemplateTag.HOLD)
                .tag(NhTemplateTag.RECIPIENT_CHANGE_REQUEST_SERVICE)
                .tag(NhTemplateTag.RESPONSE_HANDLER)
                .tag(NhTemplateTag.SENDER_CHANGE_REQUEST_SERVICE)
                .tag(NhTemplateTag.TEST)
                .build();
    }
}
