package com.aerosync.bank_link_sdk

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentActivity


class WidgetActivity: FragmentActivity() {

    internal companion object {
        const val EXTRA_URL = "url"
        const val EXTRA_SUCCESS = "success"
    }

    private lateinit var webView: WebView
    // Set once the widget has succeeded or closed; later events are ignored
    private var completed = false

    // Widget events arrive on the WebView's JavaBridge thread; handle them on the main thread
    private val eventHandler = object : EventListener {
        override fun onSuccess(event: PayloadSuccessType) = runOnUiThread {
            finishWithResult(Activity.RESULT_OK, Intent().putExtra(EXTRA_SUCCESS, event))
        }

        override fun onClose() = runOnUiThread { finishWithResult(Activity.RESULT_CANCELED) }

        override fun onEvent(event: PayloadEventType) = runOnUiThread {
            if (!completed) Widget.liveListener?.onEvent(event)
        }

        override fun onError(error: String) = runOnUiThread {
            if (!completed) Widget.liveListener?.onError(error)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        val url = intent?.getStringExtra(EXTRA_URL)
        if (url == null) {
            // Opened without the url extra (no widget to return to); close instead of crashing
            finish()
            return
        }
        initializeWebView(url)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // handle widget navigation to go back, otherwise close the widget
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finishWithResult(Activity.RESULT_CANCELED)
                }
            }
        })
    }

    // The SDK owns closing the widget: return the outcome to Widget and finish
    private fun finishWithResult(resultCode: Int, data: Intent? = null) {
        if (completed) return
        completed = true
        setResult(resultCode, data)
        finish()
    }

    private fun initializeWebView(url: String) {
        webView = findViewById<WebView>(R.id.webView);
        @SuppressLint("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true;
        // Enable DOM storage (localStorage); required by the widget's auth flow.
        webView.settings.domStorageEnabled = true;
        webView.addJavascriptInterface(WebAppInterface(eventHandler), "BankLinkSDKAndroid");
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                val intent = Intent(Intent.ACTION_VIEW, request.url)
                view.context.startActivity(intent)
                return true
            }
        }
        // Add custom headers
        val headers = mutableMapOf<String, String>()
        headers["deeplink"] = syncDeeplink(this)
        webView.loadUrl(url, headers);
    }
}
