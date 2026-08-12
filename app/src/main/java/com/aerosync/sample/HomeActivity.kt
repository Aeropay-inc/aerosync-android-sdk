package com.aerosync.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.compose.ui.text.toLowerCase
import androidx.fragment.app.FragmentActivity
import com.aerosync.bank_link_sdk.EnvironmentType
import com.aerosync.bank_link_sdk.EventListener
import com.aerosync.bank_link_sdk.PayloadEventType
import com.aerosync.bank_link_sdk.PayloadSuccessType
import com.aerosync.bank_link_sdk.Theme
import com.aerosync.bank_link_sdk.Widget

class HomeActivity : FragmentActivity(), EventListener {

    var selectedEnvironment: EnvironmentType = EnvironmentType.SANDBOX
    var defaultTheme: Theme = Theme.LIGHT
    var manualLinkOnly=  false
    var handleMfa=  false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val dropdown = findViewById<Spinner>(R.id.spinner)
        val themeDropdown = findViewById<Spinner>(R.id.theme)
        //create a list of items for the spinner.
        val items = EnvironmentType.values().map {  it.name.lowercase().replaceFirstChar { char -> char.uppercase() }  }
        val adapter: Any? = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = adapter as SpinnerAdapter?
        dropdown?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedEnvironment = EnvironmentType.values()[position]
            }
        }
        //create a list of items for the theme.
        val themeItems = Theme.values().map {  it.name.lowercase().replaceFirstChar { char -> char.uppercase() }  }
        val themeAdapter: Any? = ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_dropdown_item, themeItems)
        themeDropdown.adapter = themeAdapter as SpinnerAdapter?
        themeDropdown?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                defaultTheme = Theme.values()[position]
            }
        }
        val manualLinkOnlyId: SwitchCompat = findViewById(R.id.manual_link_only)
        manualLinkOnlyId.setOnCheckedChangeListener { _, isChecked ->
            this.manualLinkOnly = isChecked
        }
        val handleMFAId: SwitchCompat = findViewById(R.id.handle_Mfa_only)
        handleMFAId.setOnCheckedChangeListener { _, isChecked ->
            this.handleMfa = isChecked
            val jobIdField = findViewById<EditText>(R.id.jobId)
            val connectionIdField = findViewById<EditText>(R.id.connectionId)

            if (isChecked) {
                jobIdField.visibility = View.VISIBLE
                connectionIdField.visibility = View.VISIBLE
            } else {
                jobIdField.visibility = View.GONE
                connectionIdField.visibility = View.GONE
            }
        }
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                val token = findViewById<EditText>(R.id.token).text;
                val aeroPassUserUuid = findViewById<EditText>(R.id.aeropassUserUuid).text;
                val configurationId = findViewById<EditText>(R.id.configurationId).text;
                val jobId = findViewById<EditText>(R.id.jobId).text;
                val connectionId = findViewById<EditText>(R.id.connectionId).text;
                val widget = Widget(this, this);

                if(token.isNullOrEmpty()) {
                    Toast.makeText(this, "Token is required!", Toast.LENGTH_SHORT).show()
                    return
                }

                if(aeroPassUserUuid.isNullOrEmpty()) {
                    Toast.makeText(this, "AeroPass ID is required!", Toast.LENGTH_SHORT).show()
                    return
                }

                widget.environment = selectedEnvironment //SANDBOX, PROD
                widget.token = token.toString();
                widget.manualLinkOnly = this.manualLinkOnly
                widget.handleMFA = this.handleMfa
                widget.aeroPassUserUuid = aeroPassUserUuid.toString()
                widget.configurationId = configurationId.toString();
                widget.jobId = jobId.toString();
                widget.connectionId = connectionId.toString();
                widget.defaultTheme = defaultTheme
                widget.open();
            }
        }
    }

    override fun onSuccess(event: PayloadSuccessType?, context: Context?) {
        // perform steps when user have completed the bank link workflow
        // sample code
        if (event != null) {
            val accounts = event.accounts
            if (accounts != null) {
                // multi-account: one entry per linked account
                val summary = accounts.joinToString(", ") { it.connectionId }
                Toast.makeText(context, "connectionIds = $summary, " +
                        "clientName = ${event.clientName}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,  "connectionId = ${event.connectionId}, " +
                        "AeroPassId = ${event.aeroPassUserUuid}, " +
                        "clientName = ${event.clientName}", Toast.LENGTH_SHORT).show()
            }
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