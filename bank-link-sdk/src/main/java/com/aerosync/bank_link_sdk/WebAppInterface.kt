package com.aerosync.bank_link_sdk

import android.webkit.JavascriptInterface
import org.json.JSONObject
import java.lang.Exception

// Parses the widget's events and reports them to WidgetActivity
internal class WebAppInterface(private val eventListener: EventListener) {
    @JavascriptInterface
    fun streamEvents(event: String) {
        if(event.isEmpty()) return;
        val response: JSONObject
        try {
            response = JSONObject(event)
        } catch(e: Exception) {
            eventListener.onError("Unable to parse sync event: $e")
            return;
        }
        if(response.length() == 0) return
        val widgetEventType = WidgetEventType.fromEvent(response.optString("type"))
        if(widgetEventType == null) {
            eventListener.onError("Invalid widget event type: ${response.optString("type")}")
            return
        }
        try {
            when (widgetEventType) {
                WidgetEventType.WIDGET_PAGE_SUCCESS -> {
                    val payload = response.getJSONObject("payload")
                    val accountsJson = payload.optJSONArray("accounts")
                    val payloadSuccess = if (accountsJson != null) {
                        // multi-account: build one entry per linked account
                        val accounts = (0 until accountsJson.length()).map {
                            val account = accountsJson.getJSONObject(it)
                            PayloadSuccessAccount(
                                connectionId = account.getString("connectionId"),
                                accountType = account.getString("accountType"),
                                accountNumberDisplay = account.getString("accountNumberDisplay"),
                            )
                        }
                        PayloadSuccessType(
                            connectionId = null,
                            clientName = payload.getString("clientName"),
                            aeroPassUserUuid = payload.getString("aeroPassUserUuid"),
                            accounts = accounts,
                        )
                    } else {
                        // single account
                        PayloadSuccessType(
                            connectionId = payload.getString("connectionId"),
                            clientName = payload.getString("clientName"),
                            aeroPassUserUuid = payload.getString("aeroPassUserUuid"),
                        )
                    }
                    eventListener.onSuccess(payloadSuccess)
                }
                WidgetEventType.WIDGET_PAGE_LOADED
                -> {
                    val payload = response.getJSONObject("payload")
                    val payloadEvent = PayloadEventType(
                        pageTitle = payload.optString("pageTitle"),
                        onLoadApi = payload.optString("onLoadApi"),
                    )
                    eventListener.onEvent(payloadEvent)
                }
                WidgetEventType.WIDGET_CLOSE -> eventListener.onClose()
                WidgetEventType.WIDGET_ERROR -> {
                    // payload is usually a plain string; objects are passed on as JSON
                    eventListener.onError(response.opt("payload")?.toString() ?: "")
                }
            }
        } catch(e: Exception) {
            eventListener.onError("Error in widget event callback: $e")
        }

    }
}
