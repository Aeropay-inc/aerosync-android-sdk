package com.aerosync.bank_link_sdk

import android.content.Context

const val ERROR_WIDGET_LOAD = "Unable to load the Aerosync widget"
const val SYNC_VERSION = "2.1.0"

// Return deeplink, unique per app. Must match RedirectActivity's intent filter.
internal fun syncDeeplink(context: Context) = "aerosync://bank-link/${context.packageName}"
