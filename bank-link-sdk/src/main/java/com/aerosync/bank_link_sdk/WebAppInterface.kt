package com.aerosync.bank_link_sdk

import android.webkit.JavascriptInterface
import android.content.Context
import com.google.gson.Gson
import java.lang.Exception

class WebAppInterface(private val mContext: Context, private var eventListener: EventListener) {
    @JavascriptInterface
    fun streamEvents(event: String) {
        if(event.isEmpty()) return;
        var response: StreamEvent;
        try {
            response = Gson().fromJson(event, StreamEvent::class.java)
        } catch(e: Exception) {
            eventListener.onError("Unable to parse sync event: $e", mContext)
            return;
        }
        val exists = WidgetEventType.values().any { it.name == response.type.toString() }
        if(!exists) return;

        try {
            when (response.type) {
                WidgetEventType.WIDGET_PAGE_SUCCESS -> eventListener.onSuccess(response.payload.toString(), mContext)
                WidgetEventType.WIDGET_PAGE_LOADED, WidgetEventType.UNKNOWN
                -> eventListener.onEvent(response.type, response.payload.toString(), mContext)
                WidgetEventType.WIDGET_CLOSE -> eventListener.onClose(mContext)
                WidgetEventType.WIDGET_ERROR -> eventListener.onError(response.payload.toString(), mContext)
            }
        } catch(e: Exception) {
            eventListener.onError("Error in widget event callback: $e", mContext)
        }

    }
}