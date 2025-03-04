package com.aerosync.sample

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.aerosync.bank_link_sdk.EnvironmentType
import com.aerosync.bank_link_sdk.EventListener
import com.aerosync.bank_link_sdk.PayloadEventType
import com.aerosync.bank_link_sdk.PayloadSuccessType
import com.aerosync.bank_link_sdk.Widget

class HomeActivity : FragmentActivity(), EventListener {

    var selectedEnvironment: EnvironmentType = EnvironmentType.STAGE
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
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                // open Aerosync widget
                val token = findViewById<EditText>(R.id.token).text;
                val configurationId = findViewById<EditText>(R.id.configurationId).text;
                val aeroPassUserUuid = findViewById<EditText>(R.id.aeroPassUserUuid).text;
                val envSpinner = findViewById<View>(R.id.spinner) as Spinner
                val config = Widget(this, this);
                config.environment = selectedEnvironment //STAGE, SANDBOX, PROD
                config.token = token.toString();
                config.configurationId = configurationId.toString();
                config.aeroPassUserUuid = aeroPassUserUuid.toString();
                config.open();
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