package com.aerosync.bank_link_sdk

import android.webkit.JavascriptInterface
import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.lang.Exception

class WebAppInterface(private val mContext: Context, private var eventListener: EventListener) {
    @JavascriptInterface
    fun streamEvents(event: String) {
        if(event.isEmpty()) return;
        var response: JsonObject;
        try {
            response = Gson().fromJson(event, JsonObject::class.java)
        } catch(e: Exception) {
            eventListener.onError("Unable to parse sync event: $e", mContext)
            return;
        }
        if(response.isEmpty) return
        var widgetEventType: WidgetEventType? = null;
        try{
            widgetEventType = WidgetEventType.fromEvent(response.get("type").getAsString())!!
        } catch (e: Exception) {
            eventListener.onError("Invalid widget event type: $e", mContext)
        }
        if(widgetEventType == null) return
        try {
            when (widgetEventType) {
                WidgetEventType.WIDGET_PAGE_SUCCESS -> {
                    val payloadSuccess = PayloadSuccessType(
                        user_id = response.get("payload").asJsonObject.get("user_id").toString(),
                        user_password = response.get("payload").asJsonObject.get("user_password").toString(),
                        ClientName = response.get("payload").asJsonObject.get("ClientName").toString(),
                        FILoginAcctId = response.get("payload").asJsonObject.get("FILoginAcctId").toString()
                    )
                    eventListener.onSuccess(payloadSuccess, mContext)
                }
                WidgetEventType.WIDGET_PAGE_LOADED
                -> {
                    val payloadEvent = PayloadEventType(
                        pageTitle = response.get("payload").asJsonObject.get("pageTitle").toString(),
                        onLoadApi = response.get("payload").asJsonObject.get("onLoadApi").toString(),
                    )
                    eventListener.onEvent(payloadEvent, mContext)
                }
                WidgetEventType.WIDGET_CLOSE -> eventListener.onClose(mContext)
                WidgetEventType.WIDGET_ERROR -> {
                    eventListener.onError(response.get("payload").toString(), mContext)
                }
            }
        } catch(e: Exception) {
            eventListener.onError("Error in widget event callback: $e", mContext)
        }

    }
}