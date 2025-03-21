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
//  https://github.com/Aeropay-inc/aerosync-android-sdk/blob/master/app/src/main/java/com/aerosync/sample/HomeActivity.kt

package com.aerosync.sample

class HomeActivity : FragmentActivity(), EventListener {

    var selectedEnvironment: EnvironmentType = EnvironmentType.STAGE
    var manualLinkOnly=  false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val dropdown = findViewById<Spinner>(R.id.spinner)
        //create a list of items for the spinner.
        val items = EnvironmentType.values().map { it.name }
        val adapter: Any? = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter as SpinnerAdapter?
        dropdown?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEnvironment = EnvironmentType.values()[position]
            }
        }
        val manualLinkOnlyId: SwitchCompat = findViewById(R.id.manual_link_only)
        manualLinkOnlyId.setOnCheckedChangeListener { _, isChecked ->
            this.manualLinkOnly = isChecked
        }
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                val token = findViewById<EditText>(R.id.token).text;
                val consumerId = findViewById<EditText>(R.id.consumerId).text;
                val widget = Widget(this, this);
                widget.environment = selectedEnvironment //STAGE, SANDBOX, PROD
                widget.token = token.toString();
                widget.manualLinkOnly = this.manualLinkOnly
                widget.consumerId = consumerId.toString();
                widget.open();
            }
        }
    }

    override fun onSuccess(event: PayloadSuccessType?, context: Context?) {
        // perform steps when user have completed the bank link workflow
        // sample code
        if (event != null) {
            Toast.makeText(context,  "user = ${event.user_id}, " +
                    "ClientName = ${event.ClientName}, " +
                    "FILoginAcctId = ${event.FILoginAcctId}", Toast.LENGTH_SHORT).show()

        };
        val intent = Intent(context, HomeActivity::class.java)
        context?.startActivity(intent);
        val output = findViewById<TextView>(R.id.output);
        output.text = event.toString();

    }

    override fun onEvent(event: PayloadEventType?, context: Context?) {
        // capture all the Aerosync events
        // sample code
        if (event != null) {
            Toast.makeText(context, "ONEVENT: onLoadApi = ${event.onLoadApi},\" +\n" +
                    "                    \"pageTitle = ${event.pageTitle}", Toast.LENGTH_SHORT).show()

        };
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
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent);
        (context as Activity).finish()
    }
}

```

# 4. Bank Link SDK configuration and Aerosync-UI Response:

https://api-aeropay.readme.io/docs/android-sdk#4-bank-link-sdk-configuration




