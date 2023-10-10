package com.aerosync.bank_link_sdk

import android.content.Context
import android.webkit.JavascriptInterface
import com.google.gson.Gson
class WebAppInterface(private val mContext: Context, private var eventListener: EventListener) {
    @JavascriptInterface
    fun streamEvents(event: String) {
        if(!event.isNullOrEmpty()) {
            val gson = Gson();
            val response: StreamEvent = gson.fromJson(event, StreamEvent::class.java);
            if(!response.type.isNullOrEmpty() &&
                response.payload.toString().isNotEmpty()
            ) {
                when (response.type) {
                    "pageSuccess" -> eventListener.onSuccess(response.payload.toString(), mContext)
                    "widgetPageLoaded" -> eventListener.onEvent(response.type, response.payload.toString(), mContext)
                    "widgetClose" -> eventListener.onClose(mContext)
                    "widgetError" -> eventListener.onError(response.payload.toString(), mContext)
                }
            }

        }
    }
}