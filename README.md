# aerosync-android-sdk

Aerosync android library

# Introduction

This Android SDK provides an interface to load Aerosync-UI in native Android application. Securely link your bank account through your bank’s website. Log in with a fast, secure, and tokenized connection. Your information is never shared or sold.

Full reference: https://sync.dev.aero.inc/docs/android-aeronetwork

# Requirements

| | |
| --- | --- |
| `minSdk` | 24 |
| `compileSdk` | 34 or higher (required from 2.1.0) |

No code changes are needed in your app beyond the integration below. The SDK
configures the WebView itself.

# 1. Install Bank-Link-Sdk

Add latest verion of _com.aerosync/bank-link-sdk_ library to your project dependencies.

Maven Central:
https://central.sonatype.com/artifact/com.aerosync/bank-link-sdk/overview
https://repo1.maven.org/maven2/

```
<dependency>
    <groupId>com.aerosync</groupId>
    <artifactId>bank-link-sdk</artifactId>
    <version>2.1.0</version>
</dependency>
```

```
implementation group: 'com.aerosync', name: 'bank-link-sdk', version: '2.1.0'
```

# 2. Widget configuration

| Property | Type | Required | Notes |
| --- | --- | --- | --- |
| `token` | String | Yes | From the `GET /aggregatorCredentials` endpoint |
| `environment` | EnvironmentType | Yes | `STAGE`, `SANDBOX` or `PROD`. Defaults to `PROD` |
| `aeroPassUserUuid` | String | Yes | AeroNetwork user ID |
| `configurationId` | String | No | Client customization identifier |
| `handleMFA` | Boolean | No | For balance refresh workflows |
| `jobId` | String | No | Required when `handleMFA` is true |
| `connectionId` | String | No | Required when `handleMFA` is true |
| `manualLinkOnly` | Boolean | No | Restrict to manual account linking |
| `defaultTheme` | Theme | No | `Theme.LIGHT` or `Theme.DARK` |

The deeplink back into the widget (`aerosync://bank-link`) is registered by the
SDK, there is nothing to add to your manifest for it.

# 3. Minimal example to implement bank-link-sdk

**AndroidManifest.xml**

```
 <uses-permission android:name="android.permission.INTERNET"/>
```

**HomeActivity.kt**

```kotlin
//  https://github.com/Aeropay-inc/aerosync-android-sdk/blob/master/app/src/main/java/com/aerosync/sample/HomeActivity.kt

package com.aerosync.sample

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.aerosync.bank_link_sdk.EnvironmentType
import com.aerosync.bank_link_sdk.EventListener
import com.aerosync.bank_link_sdk.PayloadEventType
import com.aerosync.bank_link_sdk.PayloadSuccessType
import com.aerosync.bank_link_sdk.Theme
import com.aerosync.bank_link_sdk.Widget

class HomeActivity : FragmentActivity(), EventListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                val widget = Widget(this, this)
                widget.environment = EnvironmentType.PROD
                widget.token = "<TOKEN>"
                widget.aeroPassUserUuid = "<AERONETWORK USER ID>"
                widget.configurationId = "<CONFIGURATION ID>"
                widget.defaultTheme = Theme.LIGHT
                widget.open()
            }
        }
    }

    override fun onSuccess(event: PayloadSuccessType?, context: Context?) {
        // user completed the bank link workflow
        if (event != null) {
            Toast.makeText(
                context,
                "connectionId = ${event.connectionId}, " +
                    "clientName = ${event.clientName}, " +
                    "aeroPassUserUuid = ${event.aeroPassUserUuid}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onEvent(event: PayloadEventType?, context: Context?) {
        // all Aerosync events
        if (event != null) {
            Toast.makeText(
                context,
                "pageTitle = ${event.pageTitle}, onLoadApi = ${event.onLoadApi}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onError(error: String?, context: Context) {
        Toast.makeText(context, "onError--> $error", Toast.LENGTH_SHORT).show()
    }

    override fun onClose(context: Context) {
        // widget closed by the user
        Toast.makeText(context, "widget closed", Toast.LENGTH_SHORT).show()
    }
}

```

# 4. Events

| Callback | Payload | Fires when |
| --- | --- | --- |
| `onSuccess` | `PayloadSuccessType(connectionId, clientName, aeroPassUserUuid)` | The bank link workflow completed |
| `onEvent` | `PayloadEventType(pageTitle, onLoadApi)` | Any widget page event |
| `onError` | `String` | The widget failed to load or errored |
| `onClose` | none | The user closed the widget |

# 5. Passkeys

From 2.1.0 the widget can complete two factor authentication with a passkey
inside the WebView instead of handing off to an external browser. This is
automatic and requires no integration work. It applies when all of the following
are true, and falls back to the external browser flow otherwise:

-   the device has a recent Android System WebView (the SDK checks
    `WebViewFeature.WEB_AUTHENTICATION` at runtime)
-   the user has a screen lock or biometrics enrolled
-   the user has a passkey, or chooses to create one

# 6. Bank Link SDK configuration and Aerosync-UI Response:

https://api-aeropay.readme.io/docs/android-sdk#4-bank-link-sdk-configuration
