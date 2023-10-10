package com.aerosync.bank_link_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.io.Serializable

data class Widget(
    @Transient var context: Context,
    var environment: String? = null,
    var deeplink: String? = null,
    var consumerId: String? = null,
    var token: String? = null,
    var url: String? = null,
    var eventListener: EventListener

): Serializable {

    constructor(activity: Activity, eventListener: EventListener) : this(context = activity, eventListener = eventListener) {
        // Aerosync pre-defined deeplink
        // no need to change this value
        this.deeplink = "aerosync://bank-link"
    }

    fun open() {
        if(environment != null &&
           deeplink != null &&
           token !==null) {
            url = constructUrl();
            val intent = Intent(context, WidgetActivity()::class.java);
            intent.putExtra("widget", this)
            context.startActivity(intent);
        }
    }

    protected fun constructUrl(): String {
        val checkEnv = EnvironmentType.values().any { it.name == environment }
        return if (!consumerId.isNullOrEmpty() && checkEnv) {
            "${EnvironmentType.valueOf(environment!!).value}/?token=${token}" +
                    "&deeplink=${deeplink}&consumerId=${consumerId}";
        } else if (checkEnv) {
            "${EnvironmentType.valueOf(environment!!).value}/?token=${token}&deeplink=${deeplink}";
        } else {
            "${EnvironmentType.valueOf("PROD").value}/?token=${token}&deeplink=${deeplink}"
        }
    }
}
