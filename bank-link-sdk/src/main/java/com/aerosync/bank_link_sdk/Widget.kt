package com.aerosync.bank_link_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log

data class Widget(
    var context: Context,
    var environment: String? = null,
    var configurationId: String? = null,
    var token: String? = null,
    var url: String? = null,
    var handleMFA: Boolean? = false,
    var jobId: String? = null,
    var userId: String? = null,
    var aeroPassUserUuid: String? = null,
    var eventListener: EventListener,
    ) {

    companion object {
        lateinit var eventObj: EventListener
    }

    constructor(activity: Activity, eventListener: EventListener) : this(context = activity, eventListener = eventListener) {
        eventObj = eventListener;
    }

    fun open() {
        if(environment != null &&
           token !==null) {
            url = constructUrl();
            val intent = Intent(context, WidgetActivity::class.java);
            intent.putExtra("url", this.url)
            context.startActivity(intent);
        }
    }

    protected fun constructUrl(): String {
        val checkEnv = EnvironmentType.values().any { it.name == environment }
        var baseUrl = "${EnvironmentType.valueOf("PROD").value}/?token=${token!!}&deeplink=${SYNC_DEEPLINK}"
        if(checkEnv) {
            baseUrl = "${EnvironmentType.valueOf(environment!!).value}/?token=${token!!}&deeplink=${SYNC_DEEPLINK}";
        }

        if(checkEnv && !configurationId.isNullOrEmpty()) {
            baseUrl = "${baseUrl}&consumerId=${configurationId}";
        }

        if(checkEnv && !aeroPassUserUuid.isNullOrEmpty()) {
            baseUrl = "${baseUrl}&aeroPassUserUuid=${aeroPassUserUuid}";
        }
        
        if(checkEnv && handleMFA == true) {
            baseUrl = "${baseUrl}&handleMFA=${handleMFA}&jobId=${jobId}&userId=${userId}";
        }

        return baseUrl;
    }
}
