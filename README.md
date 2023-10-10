# aerosync-android-sdk
Aerosync android library

# Introduction
This Android SDK provides an interface to load Aerosync-UI in native Android application. Securely link your bank account through your bank’s website. Log in with a fast, secure, and tokenized connection. Your information is never shared or sold.

# 1. Install Bank-Link-Sdk

Add latest verion of _com.aerosync/bank-link-sdk_ library to your project dependencies.

Maven Central: 
https://central.sonatype.com/artifact/com.aerosync/bank-link-sdk/overview
https://repo1.maven.org/maven2/

```
<dependency>
    <groupId>com.aerosync</groupId>
    <artifactId>bank-link-sdk</artifactId>
    <version>1.0.1</version>
</dependency>
```

```
implementation group: 'com.aerosync', name: 'bank-link-sdk', version: '1.0.1'
```

# 2. Minimal example to implement bank-link-sdk

**AndroidManifest.xml**
```
 <uses-permission android:name="android.permission.INTERNET"/>
```

**Homepage.kt**
```
package com.aerosync.test_client_android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aerosync.bank_link_sdk.EventListener
import com.aerosync.bank_link_sdk.Widget

class Homepage : AppCompatActivity(), EventListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_homepage)
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                var config = Widget(this, this);
                config.environment = "PROD"; //DEV, STAGE,PROD
                config.deeplink = "aerosync://bank-link";
                config.token = "<PROD TOKEN>";
                config.open();
            }
        }
    }

    override fun onSuccess(response: String?, context: Context) {
        // perform steps when user have completed the bank link workflow
        // sample code
        Toast.makeText(context, "onSuccess--> $response", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, Homepage::class.java)
        context.startActivity(intent);
    }

    override fun onEvent(type: String?, payload: String?, context: Context) {
        // capture all the Aerosync events
        // sample code
        Toast.makeText(context, "onEvent--> $payload", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: String?, context: Context) {
        // error handling
        // sample code
        Toast.makeText(context, "onError--> $error", Toast.LENGTH_SHORT).show()
    }

    override fun onClose(context: Context) {
        // when widget is closed by user
        // sample code
        Toast.makeText(context,"widget closed", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, Homepage::class.java)
        context.startActivity(intent);
    }
}

```

# 4. Bank Link SDK configuration and Aerosync-UI Response:

https://api-aeropay.readme.io/docs/android-sdk#4-bank-link-sdk-configuration




