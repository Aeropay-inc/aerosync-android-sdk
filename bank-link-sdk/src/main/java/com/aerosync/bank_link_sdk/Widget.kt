package com.aerosync.bank_link_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.lang.StringBuilder

data class Widget(
    var context: Context,
    var environment: EnvironmentType,
    var configurationId: String? = null,
    var token: String? = null,
    var handleMFA: Boolean = false,
    var manualLinkOnly: Boolean = false,
    var jobId: String? = null,
    var userId: String? = null,
    var eventListener: EventListener,
    ) {

    companion object {
        lateinit var eventObj: EventListener
    }

    constructor(activity: Activity, eventListener: EventListener) : this(context = activity, eventListener = eventListener, environment = EnvironmentType.PROD) {
        eventObj = eventListener;
    }

    fun open() {
        try {
            val url = constructUrl(mapOf(
                "token" to token,
                "deeplink" to SYNC_DEEPLINK,
                "configurationId" to configurationId,
                "handleMFA" to handleMFA.toString(),
                "manualLinkOnly" to manualLinkOnly.toString()

            ));
            val intent = Intent(context, WidgetActivity::class.java);
            intent.putExtra("url", url)
            context.startActivity(intent);
        } catch (e: Exception) {
            eventObj.onError("Error | $ERROR_WIDGET_LOAD | ${e.message}", context)
        }
    }

    private fun constructUrl(params: Map<String, String?>): String {

        var baseUrl = StringBuilder(environment.value)

        val configsItr = params.iterator()
        if(configsItr.hasNext()) {
            baseUrl.append("?")
            configsItr.forEach { (key, value) ->
                if(!value.isNullOrEmpty()) {
                    baseUrl.append("${key}=${value}&")
                }
            }
        }
        baseUrl.deleteCharAt(baseUrl.length - 1)
        return baseUrl.toString();
    }
}
