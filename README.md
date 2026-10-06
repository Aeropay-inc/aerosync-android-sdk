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
    <version>3.0.0</version>
</dependency>
```

```
implementation 'com.aerosync:bank-link-sdk:3.0.0'
```

# 2. Quick start

**AndroidManifest.xml**

```
 <uses-permission android:name="android.permission.INTERNET"/>
```

Nothing else is needed in your manifest. The SDK registers its return deeplink
(`aerosync://bank-link/<your applicationId>`) automatically.

**1. Create the widget with your screen.** Your screen must be an `AppCompatActivity`,
`FragmentActivity`, `ComponentActivity` or a `Fragment`. Create the `Widget` as a field
(or in `onCreate`), not in a click handler. This is what lets `onSuccess` / `onClose`
reach your screen even if Android killed your app while the user was in their bank app.

**2. Open it** with a `WidgetConfiguration`.

**3. Handle the events.** `onSuccess` and `onClose` are called after the widget has
closed itself; you do not need to close it. All callbacks run on the main thread.

```kotlin
// Full sample: app/src/main/java/com/aerosync/sample/HomeActivity.kt

class HomeActivity : AppCompatActivity(), EventListener {

    private val widget = Widget(this, this)

    fun onLinkBankClicked() {
        widget.open(
            WidgetConfiguration(
                token = token,
                environment = EnvironmentType.SANDBOX, // SANDBOX, PROD (default)
                aeroPassUserUuid = aeroPassUserUuid,
                // optional: configurationId, handleMFA, manualLinkOnly,
                // jobId, connectionId, defaultTheme
            )
        )
    }

    override fun onSuccess(event: PayloadSuccessType) {
        // user completed the bank link workflow
        val accounts = event.accounts
        if (accounts != null) {
            // multi-account: read accounts (connectionId, accountType,
            // accountNumberDisplay). The top-level connectionId is null here.
        } else {
            // single account: connectionId is always present
        }
    }

    override fun onClose() {
        // user closed the widget
    }

    override fun onEvent(event: PayloadEventType) {
        // page events while the widget is open (pageTitle, onLoadApi)
    }

    override fun onError(error: String) {
        // errors while the widget is open; the user may be able to continue
    }
}
```

**From a Fragment**, pass the Fragment instead: `private val widget = Widget(this, listener)`.

**Other ways to pass the listener.** Your screen does not have to implement `EventListener`:

```kotlin
// Inline
private val widget = Widget(this, object : EventListener { /* ... */ })

// Separate property: declare the listener BEFORE the widget. Kotlin initializes
// properties top to bottom, so the other order passes null and crashes.
private val listener = object : EventListener { /* ... */ }
private val widget = Widget(this, listener)
```

Inside `object : EventListener { }`, `this` is the listener; use `this@YourActivity` for the screen.

# 3. Migrating from 2.x

3.0.0 moves the widget to the Activity Result API, so the result survives Android
killing your app during the bank login. Changes:

1. Your screen must be an `AppCompatActivity` / `FragmentActivity` / `ComponentActivity` or a `Fragment`.
2. Create `Widget(this, this)` as a field or in `onCreate`, not in your click handler.
3. Pass the options to `open()` instead of setting properties on the widget:
   ```kotlin
   // 2.x
   widget.environment = EnvironmentType.PROD
   widget.token = token
   widget.open()
   // 3.0.0
   widget.open(WidgetConfiguration(token = token, environment = EnvironmentType.PROD))
   ```
4. Remove the `context` parameter from your callbacks; the payloads are no longer nullable:

   | 2.x | 3.0.0 |
   |---|---|
   | `onSuccess(event: PayloadSuccessType?, context: Context?)` | `onSuccess(event: PayloadSuccessType)` |
   | `onEvent(event: PayloadEventType?, context: Context?)` | `onEvent(event: PayloadEventType)` |
   | `onError(error: String?, context: Context)` | `onError(error: String)` |
   | `onClose(context: Context)` | `onClose()` |
5. The SDK now closes the widget itself. Remove `(context as Activity).finish()` from
   `onClose` and any code that re-launches your own screen from `onSuccess`.
   In 3.0.0 there is no widget context to finish, and finishing your own screen would close it.
6. `onEvent` values and `onError` messages no longer include JSON quote marks
   (`/verify` instead of `"/verify"`). Remove any code that strips them.
7. The return deeplink is now unique per app (`aerosync://bank-link/<applicationId>`).
   Nothing to change on your side.

# 4. Bank Link SDK configuration and Aerosync-UI Response:

https://api-aeropay.readme.io/docs/android-sdk#4-bank-link-sdk-configuration

# 5. Releasing a new version

Maintainers: see [RELEASING.md](RELEASING.md) for the Maven Central publishing steps.
