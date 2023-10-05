package com.aerosync.bank_link_sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.io.Serializable
import android.util.Log

data class Widget(
    @Transient var context: Context,
    var environment: String? = null,
    var deeplink: String? = null,
    var consumerId: String? = null,
    var token: String? = null,
    var url: String? = null,
): Serializable {

    constructor(activity: Activity) : this(context = activity)

    fun open() {
        if(environment != null &&
           deeplink != null &&
           token !==null) {
            url = constructUrl();
            val intent = Intent(context, WidgetActivity::class.java);
            intent.putExtra("widget", this)
            context.startActivity(intent);
        }
    }

    protected fun constructUrl(): String {
        val checkEnv = EnvironmentType.values().any { it.name == environment }
        return if (checkEnv) {
            "${EnvironmentType.valueOf(environment!!).value}/?token=${token}&deeplink=${deeplink}";
        } else { ""; }
    }
}
