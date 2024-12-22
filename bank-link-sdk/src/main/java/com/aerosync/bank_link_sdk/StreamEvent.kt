package com.aerosync.bank_link_sdk

import com.google.gson.annotations.SerializedName

class StreamEvent(
    val type: WidgetEventType,
    val payload: Any
) {}


enum class WidgetEventType {

    @SerializedName("pageSuccess")
    WIDGET_PAGE_SUCCESS,

    @SerializedName("widgetPageLoaded")
    WIDGET_PAGE_LOADED,

    @SerializedName("widgetClose")
    WIDGET_CLOSE,

    @SerializedName("widgetError")
    WIDGET_ERROR,

    UNKNOWN;
}