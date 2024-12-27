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

data class PayloadSuccessType(
    val user_id: String,
    val user_password: String,
    val ClientName: String,
    val FILoginAcctId: String
)

data class PayloadEventType(
    val pageTitle: String,
    val onLoadApi: String,
)