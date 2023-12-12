package com.aerosync.bank_link_sdk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class WidgetActivity: AppCompatActivity() {

    protected var url: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        initializeWebView()
    }

    protected fun initializeWebView() {
        val intent = intent ?: return
        @Suppress("DEPRECATION")
        val url: String = intent.getSerializableExtra("url") as String;
        val webView = findViewById<WebView>(R.id.webView);
        @SuppressLint("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true;
        webView.addJavascriptInterface(WebAppInterface(this, Widget.eventObj), "BankLinkSDKAndroid");
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if (request.url.toString().contains("aerosync.com")) {
                    view.loadUrl(request.url.toString())
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, request.url)
                    view.context.startActivity(intent)
                }
                return true
            }
        }
        webView.loadUrl(url!!);
    }
}