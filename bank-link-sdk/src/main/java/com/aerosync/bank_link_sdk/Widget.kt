package com.aerosync.bank_link_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent

data class Widget(
    var context: Context,
    var environment: String? = null,
    var deeplink: String? = null,
    var configurationId: String? = null,
    var token: String? = null,
    var url: String? = null,
    var handleMFA: Boolean? = false,
    var jobId: String? = null,
    var userId: String? = null,
    var aeroPassUserUuid: String? = null,
    var eventListener: EventListener
) {

    companion object {
        lateinit var eventObj: EventListener
    }

    constructor(activity: Activity, eventListener: EventListener) : this(context = activity, eventListener = eventListener) {
        // Aerosync pre-defined deeplink
        // no need to change this value
        this.deeplink = "aerosync://bank-link";
        eventObj = eventListener;
    }

    fun open() {
        if(environment != null &&
           deeplink != null &&
           token !==null) {
            url = constructUrl();
            val intent = Intent(context, WidgetActivity::class.java);
            intent.putExtra("url", this.url)
            context.startActivity(intent);
        }
    }

    protected fun constructUrl(): String {
        val checkEnv = EnvironmentType.values().any { it.name == environment }
        var baseUrl = "${EnvironmentType.valueOf("PROD").value}/?token=${token!!}"

        if(checkEnv) {
            baseUrl = "${EnvironmentType.valueOf(environment!!).value}/?token=${token!!}";
        }

        if(checkEnv && !configurationId.isNullOrEmpty()) {
            baseUrl = "${baseUrl}&consumerId=${configurationId}";
        }

        if(checkEnv && !aeroPassUserUuid.isNullOrEmpty()) {
            baseUrl = "${baseUrl}&aeroPassUserUuid=${aeroPassUserUuid}";
        }

        if(checkEnv && !deeplink.isNullOrEmpty()) {
            baseUrl = "${baseUrl}&deeplink=${deeplink}";
        }
        
        if(checkEnv && handleMFA == true) {
            baseUrl = "${baseUrl}&handleMFA=${handleMFA}&jobId=${jobId}&userId=${userId}";
        }

        return baseUrl;
    }
}
