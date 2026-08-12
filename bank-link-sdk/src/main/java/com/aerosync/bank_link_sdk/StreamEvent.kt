package com.aerosync.bank_link_sdk

enum class WidgetEventType(val event: String) {
    WIDGET_PAGE_SUCCESS("pageSuccess"),
    WIDGET_PAGE_LOADED("widgetPageLoaded"),
    WIDGET_CLOSE("widgetClose"),
    WIDGET_ERROR("widgetError");
    companion object {
        fun fromEvent(event: String): WidgetEventType? {
            return values().find { it.event == event }
        }
    }
}

// one linked account in the multi-account success payload
data class PayloadSuccessAccount(
    val connectionId: String,
    val accountType: String,
    val accountNumberDisplay: String
)

data class PayloadSuccessType(
    val connectionId: String?,                         // single flow (null in multi)
    val clientName: String,
    val aeroPassUserUuid: String,
    val accounts: List<PayloadSuccessAccount>? = null  // multi flow (null in single)
)

data class PayloadEventType(
    val pageTitle: String,
    val onLoadApi: String,
)