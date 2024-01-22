package com.aerosync.bank_link_sdk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject





class WidgetActivity: AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var webAppInterface: WebAppInterface


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget);
        initializeWebView()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // handle widget navigation to go back
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        // default system behaviour when user exit the widget
        val jsonObject = JSONObject()
        jsonObject.put("type", "widgetClose");
        jsonObject.put("payload", JSONObject());
        webAppInterface.streamEvents(jsonObject.toString())
        return super.onKeyDown(keyCode, event)
    }

    protected fun initializeWebView() {
        val intent = intent ?: return
        @Suppress("DEPRECATION")
        val url: String = intent.getSerializableExtra("url") as String;
        webView = findViewById<WebView>(R.id.webView);
        webAppInterface = WebAppInterface(this, Widget.eventObj);
        @SuppressLint("SetJavaScriptEnabled")
        webView.settings.javaScriptEnabled = true;
        webView.addJavascriptInterface(webAppInterface, "BankLinkSDKAndroid");
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if ((request.url.toString().contains("aerosync.com")) &&
                    (!request.url.toString().contains("aerosync.com/bank/oauth-pages"))) {
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