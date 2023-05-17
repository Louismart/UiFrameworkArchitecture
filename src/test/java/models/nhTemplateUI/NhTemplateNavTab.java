package models.nhTemplateUI;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NhTemplateNavTab {

    GENERAL("nav-general-tab", "General"),
    SENDER_TAB("nav-TxSender-tab", "Sender"),
    RECIPIENT_TAB("nav-TxRecipient-tab", "Recipient"),
    CORRESCANCEL_TAB("nav-CorrCancel-tab", "Corres. Cancel"),
    CORRESCHANGE_TAB("nav-CorrChange-tab", "Corres. Change"),
    CORRESISSUE_TAB("nav-CorrIssue-tab", "Corres. Issue"),
    AGENTSINBOX_TAB("nav-AgentsInbox-tab", "Agents Inbox"),
    OPSTEAM_TAB("nav-OpsTeam-tab", "OPS Team"),
    CORRESPONDENT("nav-correspondent-tab", "Correspondent"),
    PRODUCT_RECEIVE_COUNTRY("nav-productreceivecountry-tab", "ProductReceiveCountry"),
    RECEIVE_COUNTRY("nav-receivecountry-tab", "ReceiveCountry"),
    SEND_COUNTRY("nav-sendcountry-tab", "SendCountry"),
    PRODUCT("nav-product-tab", "Product");

    private final String id;
    private final String text;
}
