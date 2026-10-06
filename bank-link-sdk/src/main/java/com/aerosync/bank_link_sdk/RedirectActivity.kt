package com.aerosync.bank_link_sdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle

// Receives the return deeplink and brings the open WidgetActivity back to the front
class RedirectActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
            Intent(this, WidgetActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        )
        finish()
    }
}
